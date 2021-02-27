package com.imooc.miaosha.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.miaosha.domain.MiaoshaUser;

public class UserUtil {
	
	private static void createUser(int count) throws Exception{
		List<MiaoshaUser> users = new ArrayList<MiaoshaUser>(count);
		//生成用户
		for(int i=0;i<count;i++) {
			MiaoshaUser user = new MiaoshaUser();
			user.setId(13000000000L+i);   //因为是长整形，所以在后面加了L
			user.setLoginCount(1);
			user.setNickname("user"+i);
			user.setRegisterDate(new Date());
			user.setSalt("1a2b3c");
			user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
			users.add(user);
		}
		System.out.println("create user");
//		//插入数据库
//		Connection conn = DBUtil.getConn();    //注意这里的时间register_date 在数据库中
//		String sql = "insert into miaosha_user(id,nickname, register_date, salt, password,login_count)values(?,?,?,?,?,?)";
//		//事务将自动提交关闭
////		conn.setAutoCommit(false);
//
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			MiaoshaUser user = users.get(i);    //第一个参数指的是SQL语句中的字段 对应
//			pstmt.setLong(1, user.getId());
//			pstmt.setString(2, user.getNickname());
//			pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//			pstmt.setString(4, user.getSalt());
//			pstmt.setString(5, user.getPassword());
//			pstmt.setInt(6, user.getLoginCount());
//			pstmt.addBatch();
//		}
//		int[] counts=pstmt.executeBatch();
//		pstmt.close();
//
////		conn.commit();  //执行完毕后，手动提交事务。
////		conn.setAutoCommit(true);  //然后再把自动提交打开，避免影响其他需要自动提交的操作。
//
//		conn.close();
//		System.out.println(counts.length);
//		System.out.println("insert to db");

		//登录，生成token
		String urlString = "http://localhost:8080/login/do_login";
		File file = new File("/Users/yuhangyuan/Documents/tokens.txt");
		if(file.exists()) {
			file.delete();
		}

		/**
		 * 我们平常创建流对象关联文件，开始读文件或者写文件都是从头开始的，不能从中间开始，
		 * 如果是多线程下载一个文件我们之前学过的FileWriter等都是无法完成的，而当前RandomAccessFile
		 * 就可以解决这个问题。因为它可以指定位置读，指定位置写的一个类。通常开发中，我们多用于多线程
		 * 下载一个大文件。
		 */
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<users.size();i++) {
			MiaoshaUser user = users.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0 ,len);
			}
			inputStream.close();
			bout.close();
			String response = new String(bout.toByteArray());
			JSONObject jo = JSON.parseObject(response);
			String token = jo.getString("data");
			System.out.println("create token : " + user.getId());

			String row = user.getId()+","+token;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();

		System.out.println("over");
	}
	
	public static void main(String[] args)throws Exception {
		createUser(5000);
	}
}
