package com.zxl.fileManage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxl.fileManage.mapper.FileMapper;
import com.zxl.fileManage.mapper.UserMapper;
import com.zxl.fileManage.pojo.File;
import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.vo.param.EmailModel;
import com.zxl.fileManage.vo.utils.EmailUtils;
import com.zxl.fileManage.vo.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;

@SpringBootTest
class FileManagementApplicationTests {
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	UserMapper userMapper;
	@Autowired
	private EmailUtils emailUtils;
	/*@Autowired
	private RedisTemplate redisTemplate;
	*/
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	FileMapper fileMapper;
	@Test
	void contextLoads() {
		String email = "www.1986535918@qq.com";
		String password = "123456Aa";
		QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.eq("email",email);
		userQueryWrapper.eq("password",password);
		User user = userMapper.selectOne(userQueryWrapper);
		System.out.println(user);
//		LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
//		userLambdaQueryWrapper.eq(User::getEmail,email);
//		userLambdaQueryWrapper.eq(User::getPassword,password);
//		User user = userMapper.selectOne(userLambdaQueryWrapper);
//		//List<User> users = userMapper.selectList(null);
		//System.out.println(user);
	}
	@Test
	public void testEmail(){
		String code="456431";
		EmailModel emailModel = new EmailModel();
		emailModel.setTo("1085723641@qq.com");
		emailModel.setSubject("Test");
		emailModel.setContent("【Test】验证码" + code );
		emailUtils.sendEmail(emailModel);
	}
	@Test
	public void testRedis(){
		ValueOperations va = redisTemplate.opsForValue();
		va.set("k2","v2");
		Object k2 = va.get("k2");
		System.out.println(k2);

	}
	@Test
	public void testRedisUtils(){
		redisUtil.set("k","v",500);
		long k1 = redisUtil.getTime("k");
		System.out.println(k1);
		Object k = redisUtil.get("k");
		System.out.println(k);

	}
	@Test
	public void testMybatis(){
		QueryWrapper<File> wrapper = new QueryWrapper<>();
		wrapper.orderByDesc("update_time");
		List<File> files = fileMapper.selectList(wrapper);
		for (File file : files) {
			System.out.println(file);
		}
	}
	@Test
	public void testMybatis1(){
		List<File> files = fileMapper.selectLikePath("/1");
		System.out.println(files);
	}
	@Test
	public void testFolder(){
		String path= "D:\\echo\\renameT、";
		java.io.File file = new java.io.File(path);
		java.io.File[] files = file.listFiles();
		for (java.io.File file1 : files) {
			String path1 = file1.getPath();
			System.out.println(path1);
		}
	}
	@Test
	public void testString(){
		String path= "/1/123";
		System.out.println(path.substring(0,path.length()));
		System.out.println(path.substring(0,path.lastIndexOf("/",path.length()-2)+1));
		System.out.println(path.substring(0, path.lastIndexOf("/",path.lastIndexOf("/")-1)+1));
	}
	@Test
	public void testREdis(){
		Set<String> keys = redisTemplate.keys("*");
		System.out.println(keys);
		ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
		stringStringValueOperations.set("key","value");
		for (String key : keys) {
			redisTemplate.delete(key);
		}
	}
}
