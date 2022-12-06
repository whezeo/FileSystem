package com.zxl.fileManage.contoller;

import com.zxl.fileManage.service.FileService;
import com.zxl.fileManage.vo.param.CopyFileParam;
import com.zxl.fileManage.vo.param.FileParam;
import com.zxl.fileManage.vo.param.FolderParam;
import com.zxl.fileManage.vo.param.RenameParam;
import com.zxl.fileManage.vo.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/fs")
public class FileController {
    @Autowired
    FileService fileService;
    @RequestMapping("list")
    public Result fileList(@RequestBody FolderParam folderParam){
        return fileService.listFiles(folderParam);
    }
    @RequestMapping("/download/{id}")
    public ResponseEntity fileDownLoad(@PathVariable Long id, HttpSession session) throws IOException {
        ResponseEntity responseEntity = fileService.downFile(id, session);
        return responseEntity;
    }

    @PostMapping("/createFolder")
    public Result createFolder(@RequestBody FolderParam folderParam){
     /* fileService.cacheEvict(folderParam.getPath()
                .substring(0,folderParam.getPath().length()-1)
                .substring(0,folderParam.getPath().lastIndexOf("/"))
                + "/" );*/
        String path = folderParam.getPath();
        fileService.cacheEvict(
                path.substring(0, path.lastIndexOf("/",
                        path.lastIndexOf("/")-1) +1)
        );
        return fileService.createFolder(folderParam.getPath());
    }

    @PostMapping("/upload")
    public Result upload(String fullPath, MultipartFile file,String overwrite){
        System.out.println(fullPath.substring(0,fullPath.lastIndexOf("/"))+"/");
        fileService.cacheEvict(fullPath.substring(0,fullPath.lastIndexOf("/"))+"/");
        return fileService.uploadFile(new FileParam(fullPath,file,overwrite));
    }

    @PostMapping("/copy")
    public Result copyFile(@RequestBody CopyFileParam copyFileParam){
        fileService.cacheEvict(copyFileParam.getTargetPath());
        return fileService.copyFile(copyFileParam);
    }
    @PostMapping("/rename")
    public Result rename(@RequestBody RenameParam renameParam){
        fileService.cacheEvict(renameParam.getFullPath().substring(0,renameParam.getFullPath().lastIndexOf("/"))+"/");
        return fileService.renameFile(renameParam.getFullPath(),renameParam.getNewName());
    }
    @PostMapping("/delete")
    public Result delete(@RequestBody FileParam fileParam){
//        System.out.println(fileParam.getFullPath()
//                .substring(0,fileParam.getFullPath()
//                        .lastIndexOf("/"))+"/");
//        fileService.cacheEvict(fileParam.getFullPath()
//                .substring(0,fileParam.getFullPath()
//                        .lastIndexOf("/"))+"/");
        String path = fileParam.getFullPath();
        fileService.cacheEvict(path);
        fileService.cacheEvict(path.substring(0,path.lastIndexOf("/",path.length()-2)+1));
        //fileService.cacheEvict();
        return fileService.deleteFile(fileParam.getFullPath());
    }
    @PostMapping("/move")
    public Result move(@RequestBody CopyFileParam moveFileParam){
        fileService.cacheEvict();
        return fileService.moveFile(moveFileParam);
    }
}
