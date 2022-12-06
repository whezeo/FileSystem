package com.zxl.fileManage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan("com.zxl.fileManage.mapper")
public class FileManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementApplication.class, args);
	}

}
