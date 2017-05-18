package com.yisa.wifi.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.yisa.wifi.util.ConfigUtil;

/**
* @author liliwei
* @date  2016年6月25日 
* 
*/ 
public class ZKUtil implements Watcher {
	private static final int SESSION_TIMEOUT = 10000;
	private static final String CONNECTION_STRING = ConfigUtil.getString("zk_server");
	private ZooKeeper zk = null;
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	public static void main(String[] args) {
		ZKUtil sample = new ZKUtil();
		sample.createConnection();
//		sample.createPath("/test4", "ccc");
 
		sample.readData("/test4");

		sample.releaseConnection();
	}

	//连接zk集群
	public  void createConnection() {
		try {
			zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, this);
			countDownLatch.await(); 
		} catch (Exception e) {
			System.out.println("连接创建失败，发生 IOException");
			e.printStackTrace();
		}

	}

	/** 
	 * 关闭ZK连接 
	 */
	public void releaseConnection() {
		if (zk != null) {
			try {
				this.zk.close();
			} catch (InterruptedException e) {
				// ignore 
				e.printStackTrace();
			}
		}
	}

	/** 
	*  创建节点 
	* @param path 节点path 
	* @param data 初始数据内容 
	* @return 
	*/
	public boolean createPath(String path, String data) {
		try {
			this.zk.create(path,  
					data.getBytes(),  
					Ids.OPEN_ACL_UNSAFE,  
					CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			System.out.println("节点创建失败，发生KeeperException");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("节点创建失败，发生 InterruptedException");
			e.printStackTrace();
		}
		return true;
	}

	/** 
	 * 更新指定节点数据内容 
	 * @param path 节点path 
	 * @param data  数据内容 
	 * @return 
	 */
	public boolean writeData(String path, String data) {
		try {
			System.out.println("更新数据成功，path：" + path + ", stat: " + this.zk.setData(path, data.getBytes(), -1));
		} catch (KeeperException e) {
			System.out.println("更新数据失败，发生KeeperException，path: " + path);
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("更新数据失败，发生 InterruptedException，path: " + path);
			e.printStackTrace();
		}
		return false;
	}
	
	  /** 
     * 删除指定节点 
     * @param path 节点path 
     */ 
    public void deleteNode( String path ) { 
        try { 
            this.zk.delete( path, -1 ); 
            System.out.println( "删除节点成功，path：" + path ); 
        } catch ( KeeperException e ) { 
            System.out.println( "删除节点失败，发生KeeperException，path: " + path  ); 
            e.printStackTrace(); 
        } catch ( InterruptedException e ) { 
            System.out.println( "删除节点失败，发生 InterruptedException，path: " + path  ); 
            e.printStackTrace(); 
        } 
    } 

	/** 
	 * 读取指定节点数据内容 
	 * @param path 节点path 
	 * @return 
	 */
	public String readData(String path) {
		try {
//			System.out.println("获取数据成功，path：" + path);
			byte[] bbb = zk.getData(path, false, null);
			return Bytes.toString(bbb);
		} catch (KeeperException e) {
			System.out.println("读取数据失败，发生KeeperException，path: " + path);
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("读取数据失败，发生 InterruptedException，path: " + path);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void process(WatchedEvent event) {
		 if (event.getState() == KeeperState.SyncConnected)
		    {
		      System.out.println("watcher received event");
		      countDownLatch.countDown();
		    }

	}
}
