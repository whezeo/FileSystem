package com.zxl.fileManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxl.fileManage.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
