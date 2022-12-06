package com.zxl.fileManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.fileManage.mapper.FileMapper;
import com.zxl.fileManage.pojo.File;
import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.service.FileService;
import com.zxl.fileManage.vo.bean.ResponseFile;
import com.zxl.fileManage.vo.param.CopyFileParam;
import com.zxl.fileManage.vo.param.FileParam;
import com.zxl.fileManage.vo.param.FolderParam;
import com.zxl.fileManage.vo.result.ErrorCode;
import com.zxl.fileManage.vo.result.Result;
import com.zxl.fileManage.vo.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
   @Autowired
   FileMapper fileMapper;
   @Value("${fileLocation}")
   public  String fileLocation;
   public  String fileHead;
    @Override
    @Cacheable(value = "listFiles",key = "#folderParam.path")
    public Result listFiles(FolderParam folderParam) {
        //获得登录用户的id
        User user = UserThreadLocal.get();
        Long userId = user.getId();
        System.out.println(userId);
        //创建user对应的目录
        fileHead = fileLocation + "/User" + userId;
        java.io.File file1 = new java.io.File(fileHead);
        if(!file1.exists()){
            file1.mkdir();
        }
        //sql条件
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        String path = folderParam.getPath();
        wrapper.eq("user_id",userId);
        //排序 文件夹在前
        wrapper.eq("path",path).orderByDesc("type");
        //根据排序方式排序
        String sortMethod = folderParam.getSortMethod();
        if("size".equals(sortMethod)){
            wrapper.orderByAsc("size");
        }else if("sizeReverse".equals(sortMethod)){
            wrapper.orderByDesc("size");
        }else if("updateTime".equals(sortMethod)){
            wrapper.orderByAsc("update_time");
        } else if("updateTimeReverse".equals(sortMethod)){
            wrapper.orderByDesc("update_time");
        }else if("alpha".equals(sortMethod)){
            wrapper.orderByAsc("name");
        }else if("alphaReverse".equals(sortMethod)){
            wrapper.orderByDesc("name");
        }
        List<File> files = fileMapper.selectList(wrapper);
        //转换数据
        ArrayList<ResponseFile> responseFiles = new ArrayList<>();
        for (File file : files) {
            ResponseFile responseFile = new ResponseFile(file.getName(),file.getLink(),file.getSize(),file.getType(),file.getUpdateTime());
            responseFiles.add(responseFile);
        }

        return Result.success(responseFiles);
    }

    @Override
    public ResponseEntity downFile(Long id, HttpSession session ) throws IOException {
        //查找需要下载的文件对象
        File file = this.getById(id);
        //文件保存在d盘的echo文件夹
        //获得下载路径
        String filePath = fileHead + file.getPath() + file.getName();

        FileInputStream is = new FileInputStream(filePath);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        //设置响应头
        MultiValueMap headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + file.getName());
        //响应状态码
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity responseEntity = new ResponseEntity<>(bytes, headers,statusCode);
        is.close();
        return responseEntity;
    }

    @Override
    public Result createFolder(String path) {
        File file = new File();
        //获得文件名称和路径
        String[] split = path.split("/");
        file.setName(split[split.length-1]);
        String filePath="";
        for (int i = 1; i < split.length-1; i++) {
            filePath+="/"+split[i];
        }
        filePath+="/";
        //设置路径
        file.setPath(filePath);
        //设置类型
        file.setType("folder");
        //设置创建时间
        file.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        file.setSize(0l);
        file.setUserId(UserThreadLocal.get().getId());
        if(!this.makeFolder(file)){
            return Result.fail(10010,"文件夹已存在");
        }
        return Result.success(null);
    }

    @Override
    public boolean makeFolder(File file) {
        String path = fileHead+file.getPath()+file.getName();
        java.io.File file1 = new java.io.File(path);
        if(!file1.mkdir()){
            return false;
        }
        //将数据插入数据库
        fileMapper.insert(file);
        return true;
    }

//    /**
//     * 上传文件夹
//     * @param fileParam
//     * @return
//     */
//    @Override
//    public Result uploadFile(FileParam fileParam) {
//        MultipartFile[] files = fileParam.getFiles();
//        for (MultipartFile file1 : files) {
//            File file = new File();
//            //获得文件名称
//            String originalFilename = file1.getOriginalFilename();
//            String fullPath = fileParam.getFullPath();
//            String path = fullPath + originalFilename;
//            file.setName(originalFilename);
//            file.setPath(path);
//            if(!this.upload(file1,fileParam.getOverwrite(),file)){
//                return Result.fail(ErrorCode.ERR_FILE_EXIST.getCode(), ErrorCode.ERR_FILE_EXIST.getMsg());
//            }
//        }
//        return Result.success(null);
//    }
    @Override
    public Result uploadFile(FileParam fileParam) {
        MultipartFile source = fileParam.getFile();
        //上传文件到服务器
        if(!this.upload(source,fileParam.getOverwrite(),fileParam.getFullPath())){
            return Result.fail(ErrorCode.ERR_FILE_EXIST.getCode(), ErrorCode.ERR_FILE_EXIST.getMsg());
        }
        return Result.success(null);

    }
    @Override
    public boolean upload(MultipartFile sourceFile,String overwrite,String fullpath) {
        //读入文件
        String path = fileHead + fullpath;
        java.io.File file1 = new java.io.File(path);
        if(file1.exists()){
            if(overwrite.equals("false")){
                return false;
            }
        }
        try {
            sourceFile.transferTo(file1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将数据保存在数据库中
        File file = new File();
        //获得文件路径和名称
        String fileName = fullpath.substring(fullpath.lastIndexOf("/")).substring(1);
        file.setName(fileName);
        String filePath = fullpath.substring(0,fullpath.length()-fileName.length());
        file.setPath(filePath);
        file.setType("file");
        file.setUserId(UserThreadLocal.get().getId());
        file.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        file.setSize(sourceFile.getSize());
        fileMapper.insert(file);
        //设置file的link
        if(file1.isFile()) {
            file.setLink("http://localhost:8080/fs/download/"+ file.getId());
            QueryWrapper<File> wrapper = new QueryWrapper<>();
            wrapper.eq("id",file.getId());
            fileMapper.update(file,wrapper);
        }
        return true;
    }
    @Override
    public Result copyFile(CopyFileParam copyFileParam) {
        String originPath = copyFileParam.getOriginPath();
        if(originPath.equals("/")){
            return Result.fail(9999,"根目录不可进行此操作");
        }
        //找到所有需要复制的文件和文件夹
        List<File>  files = fileMapper.selectLikePath(originPath);
        //文件夹复制
        File source = files.get(0);
        //保存父路径
        String parentPath = source.getPath();
        //复制文件时
        if(source.getType().equals("file")){
            String path1 =fileHead + source.getPath() + source.getName();
            String path2= fileHead + copyFileParam.getTargetPath() + source.getName();
            //文件
            java.io.File sourceFile = new java.io.File(path1);
            java.io.File targetFile = new java.io.File(path2);
            //目标文件是否存在
            if(targetFile.exists()){
                if(!copyFileParam.isOverwrite()){
                    return Result.fail(ErrorCode.ERR_FILE_EXIST.getCode(),ErrorCode.ERR_FILE_EXIST.getMsg());
                }else{
                    //删除存在的路径的数据库数据
                    fileMapper.deleteByPath(copyFileParam.getTargetPath()+source.getName());
                }
            }

            copyFile(sourceFile,targetFile);
            //保存文件信息到数据库
            source.setPath(copyFileParam.getTargetPath());
            source.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            source.setId(null);
            fileMapper.insert(source);
            source.setLink("http://localhost:8080/fs/download/" + source.getId());
            QueryWrapper<File> wrapper = new QueryWrapper<>();
            wrapper.eq("id",source.getId());
            fileMapper.update(source,wrapper);
            return Result.success(null);
        }

        //复制文件夹
        if(!this.copy(source,copyFileParam.getTargetPath(),copyFileParam.isOverwrite())){
            return Result.fail(ErrorCode.ERR_FILE_EXIST.getCode(),ErrorCode.ERR_FILE_EXIST.getMsg());
        }
        //保存文件夹信息
        source.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        source.setId(null);
        fileMapper.insert(source);
        //http://localhost:8080/fs/download/1596042003509010434
        for (int i = 1; i < files.size() ; i++) {
           File file = files.get(i);
           //获得新的文件路径
           String newPath = source.getPath() + file.getPath().substring(parentPath.length());
           file.setPath(newPath);
           //
           Timestamp timestamp = new Timestamp(System.currentTimeMillis());
           file.setUpdateTime(timestamp);
           file.setId(null);
           fileMapper.insert(file);
           //http://localhost:8080/fs/download/1596042003509010434
            if(file.getLink()!=null) {
                file.setLink("http://localhost:8080/fs/download/" + file.getId());
                QueryWrapper<File> wrapper = new QueryWrapper<>();
                wrapper.eq("id",file.getId());
                fileMapper.update(file,wrapper);
            }
        }

        return Result.success(null);
    }

    @Override
    public boolean copy(File file, String targetPath,boolean overwrite){
        String name = file.getName();
        java.io.File source = new java.io.File(fileHead + file.getPath() + file.getName());
        java.io.File target = new java.io.File(fileHead + targetPath + file.getName());
        //是否覆盖
        if(target.exists()){
            if(!overwrite){
                return false;
            }
        }
        copyDir(source,target);
        //删除覆盖的文件
        file.setPath(targetPath);
        fileMapper.deleteByPath(file.getPath() + file.getName());
        return true;
    }

    @Override
    public Result renameFile(String fullPath, String newName) {
        //查出文件
        List<File> files = fileMapper.selectLikePath(fullPath);
        File old = files.get(0);
        /**
         *使用file中的rename方法进行改名
         */
        //创建一个文件
        java.io.File file = new java.io.File(fileHead + old.getPath() + old.getName());
        if(!file.exists()){
            return Result.fail(9999,"文件不存在");
        }
        if(!file.renameTo(new java.io.File(fileHead + old.getPath() +newName))){
            //重命名失败 文件名已存在
            return Result.fail(8888,"文件名已被使用");
        }
        //更新要改名的文件夹的数据库信息
        String oldName = old.getName();
        old.setName(newName);
        old.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        fileMapper.update(old,new QueryWrapper<File>().eq("id",old.getId()));
        for (int i = 1; i < files.size(); i++) {
            File file1 = files.get(i);
            //得到新的路径
            String newPath = old.getPath() + old.getName() + file1.getPath().substring(old.getPath().length()+oldName.length());
            file1.setPath(newPath);
            fileMapper.update(file1,new QueryWrapper<File>().eq("id",file1.getId()));
        }
        return Result.success(null);
    }

    @Override
    public Result deleteFile(String fullPath) {
        String realPath = fileHead +fullPath;
        java.io.File file = new java.io.File(realPath);
        if(file.isDirectory()){
            if(!this.deleteDir(file)){
                return Result.fail(9999,"删除失败");
            }
        }else{
            file.delete();
        }
        //删除数据库数据
        fileMapper.deleteByPath(fullPath);

        return Result.success(null);
    }

    @Override
    public boolean deleteDir(java.io.File file) {
        if(!file.exists()){
            return false;
        }
        java.io.File[] files = file.listFiles();
        for (java.io.File file1 : files) {
            if(file1.isFile()){
                 file1.delete();
            }else if(file1.isDirectory()){
                deleteDir(file1);
            }
        }
        return file.delete();
    }

    @Override
    public Result moveFile(CopyFileParam moveFileParam) {
        if(!copyFile(moveFileParam).isSucceed()){
            return Result.fail(9999,"移动失败");
        }
        if(!deleteFile(moveFileParam.getOriginPath()).isSucceed()){
            return Result.fail(9999,"移动失败");
        }
        return Result.success(null);
    }
    /**
     复制文件夹
     @param file  原文件夹
     @param file1 复制后的文件夹
     */
    public static void copyDir(java.io.File file, java.io.File file1) {
        if (!file.isDirectory()) {
            return;
        }
        if (!file1.exists()) {
            file1.mkdirs();
        }
        java.io.File[] files = file.listFiles();
        for (java.io.File f : files) {
            if (f.isDirectory()) {
                copyDir(f, new java.io.File(file1.getPath(), f.getName()));
            } else if (f.isFile()) {
                copyFile(f, new java.io.File(file1.getPath(), f.getName()));
            }
        }
    }
    /**
     复制文件
     @param file   原文件
     @param file1  复制后的文件
     */
    public static void copyFile(java.io.File file, java.io.File file1) {
        try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(file1)) {
            byte[] bys = new byte[1024];
            int len;
            while ((len = fis.read(bys)) != -1) {
                fos.write(bys, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //缓存清除
    @CacheEvict(value = "listFiles", allEntries = true)
    public void cacheEvict( ){
    }

    @CacheEvict(value = "listFiles",key = "#path",allEntries = false)
    public void cacheEvict(String path){
    }
}
