package com.zxl.fileManage.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxl.fileManage.pojo.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * 找到文件或者文件夹及其文件夹下的所有文件
     * @param originPath
     * @return
     */
    List<File> selectLikePath(String originPath);
    int deleteByPath(String targetPath);
}
