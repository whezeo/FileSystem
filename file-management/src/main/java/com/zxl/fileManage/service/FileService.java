package com.zxl.fileManage.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.fileManage.pojo.File;
import com.zxl.fileManage.vo.param.CopyFileParam;
import com.zxl.fileManage.vo.param.FileParam;
import com.zxl.fileManage.vo.param.FolderParam;
import com.zxl.fileManage.vo.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


public interface FileService extends IService<File> {
    /**
     * 显示文件列表
     * @param folderParam
     * @return
     */
    Result listFiles(FolderParam folderParam);

    /**
     * 实现下载文件
     * @param id
     * @param session
     * @return
     * @throws IOException
     */
    ResponseEntity downFile(Long id, HttpSession session) throws IOException;

    /**
     * 实现创建文件夹
     * @param path
     * @return
     */
    Result createFolder(String path);

    /**
     * 创建文件夹
     * 保存文件夹内容到数据库
     * @param file
     * @return
     */
    boolean makeFolder(File file);

    /**
     * 实现文件上传
     * @param fileParam
     * @return
     */
    Result uploadFile(FileParam fileParam);

    /**
     * 上传文件到服务端
     * @param sourceFile
     * @param overwrite
     * @param fullpath
     * @return
     */
    boolean upload(MultipartFile sourceFile,String overwrite,String fullpath);

    /**
     * 复制文件
     * @param copyFileParam
     * @return
     */
    Result copyFile(CopyFileParam copyFileParam);

    /**
     * 复制文件夹
     * @param file
     * @param targetPath
     * @param overwrite
     * @return
     */
    boolean copy(File file, String targetPath,boolean overwrite);

    /**
     * 重命名文件
     * @param fullPath
     * @param newName
     * @return
     */
    Result renameFile(String fullPath, String newName);

    /**
     * 删除指定文件或者文件夹
     * @param fullPath
     * @return
     */
    Result deleteFile(String fullPath);

    /**
     * 删除文件夹
     * @param file
     * @return
     */
    boolean deleteDir(java.io.File file);

    /**
     * 移动文件
     * @param moveFileParam
     * @return
     */
    Result moveFile(CopyFileParam moveFileParam);

    /**
     * 清空缓存
     */
    void cacheEvict();
    void cacheEvict(String path);
}
