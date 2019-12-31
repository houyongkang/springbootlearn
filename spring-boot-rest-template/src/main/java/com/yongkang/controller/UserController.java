package com.yongkang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongkang.domain.ErrorBody;
import com.yongkang.domain.User;
import com.yongkang.utils.APIResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by Donghua.Chen on 2018/6/20.
 */
@Api(value = "用户", description = "用户")
@RequestMapping("/user")
@RestController
public class UserController {


    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "调用远程用户服务", notes = "调用远程用户服务")
    @RequestMapping(value = "/info/{userId}", method = RequestMethod.GET)
    public User info(@PathVariable("userId") String userId) throws IOException {
        String apiURL = "http://localhost:8081/user/info/one/" + userId;
        return restTemplate.getForObject(apiURL, User.class);
    }

    @ApiOperation(value = "获取用户", notes = "返回用户信息")
    @RequestMapping(value = "/info/one/{userId}", method = RequestMethod.GET)
    public User getInfo(@PathVariable("userId") String userId){
        User user = new User();
        user.setFirstName("Chen");
        user.setEmail("1085143002@qq.com");
        user.setId(1L);
        user.setUsername("Luis");
        return user;
    }

    @ApiOperation("立项申请API请求")
    @PostMapping("/apply/intrproj")
    public APIResponse addProjSetInfo(){
        String url = "http://127.0.0.1:8080/intf/projset";
        //-------设置请求头------
        HttpHeaders headers = new HttpHeaders();
        //设置请求类型
        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded");
        headers.setContentType(type);
        //设置appId
        String appId = "d15d52db-85d4-4216-bed6-48a6bb6bf1dd";
        headers.set("appId",appId);
        //设置时间戳
        String ts = String.valueOf(new Date().getTime());
        headers.set("ts", ts);

        //秘钥
        String secretKey = "205c3944-8699-4a3d-877a-215e9eb192c7";
        //设置签名
        headers.set("sign", this.MD5(appId + ts + secretKey));

        //------请求参数-----
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
        form.add("processTheme", "测试流程主题");//流程主题
        form.add("intrProjSetId", null);//立项编号 有值不为空
        form.add("intrProjSetName", "测试项目名称");//立项名称
        form.add("sponsor", "XXXX");//项目发起人账号(多个使用","隔开)
        form.add("sponsorName", "XXXX");//项目发起人名称(多个使用","隔开)
        form.add("preparer", "XXXX");//项目筹备人账号
        form.add("preparerName", "XXXX");//项目筹备人名称
        form.add("orgId", "00000");//部门编号
        form.add("orgName", "XXXX部门");//部门名称
        form.add("projSetType", "A");//项目类型
        form.add("projSetDesc", "XXXXXXX");//项目群描述
        form.add("businessGoals", "XXXXX");//项目群目标
        form.add("investAmount", "1000");//项目群预算（万元）
        form.add("isContainBigProj", "IS_YES");//是否包含大项目立项中,如果是否 IS_NO
        form.add("timePointG3", "2018-06-25");//计划开始日期G3
        form.add("timePointG6", "2018-09-10");//计划上线日期G6
        form.add("timePointG7", "2018-12-30");//计划验收日期G7
        form.add("useUnitId", "00000");//使用单位编号 (多个使用","隔开)
        form.add("useUnitName", "吉利控股集团");//使用单位名称 (多个使用","隔开)
        form.add("publisher", "00000");//发布目标编号（多个使用","隔开）
        form.add("publisherName", "吉利控股集团");//发布目标名称（多个使用","隔开）
        form.add("isAnnualBudget", "IS_YES");//是否在年度预算内,如果是否 IS_NO
        form.add("projSetPmo", "XXX.XX");//体系审核人账号
        form.add("projSetPmoName", "XXX");//体系审核人名称
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);
        ResponseEntity<APIResponse> responseEntity = restTemplate.postForEntity(url, httpEntity, APIResponse.class);
        return responseEntity.getBody();
    }

    @ApiOperation("进行远程文件上传")
    @PostMapping(value = "/write")
    public APIResponse write(
            @ApiParam(name = "fileType", value = "文件类型", required = true)
            @RequestParam(name = "fileType", required = true)
                    String fileType,
            @ApiParam(name = "intrProjSetName", value = "立项名称", required = true)
            @RequestParam(name = "intrProjSetName", required = true)
                    String intrProjSetName,
            @ApiParam(name = "intrProjSetId", value = "立项编号(内部项目群编号)", required = false)
            @RequestParam(name = "intrProjSetId", required = false)
                    String intrProjSetId,
            @ApiParam(name = "file", value = "文件", required = false)
            @RequestParam(name = "file", required = false)
                    MultipartFile file1,
            @ApiParam(name = "filePath", value = "文件的路径", required = false)
            @RequestParam(name = "filePath", required = false)
                    String filePath,
            @ApiParam(name = "opUserName", value = "操作人名称", required = true)
            @RequestParam(name = "opUserName", required = true)
                    String opUserName,
            @ApiParam(name = "opUserAccount", value = "操作人账号", required = true)
            @RequestParam(name = "opUserAccount", required = true)
                    String opUserAccount
    ) throws IOException {
        String url = "http://127.0.0.1:8080/intf/projset/att";
        //测试本地文件上传
        File file = new File("/Users/Winterchen/Downloads/DSC_0392.jpg");
        // ---- 设置请求头部 ----
        HttpHeaders headers = new HttpHeaders();

        //设置请求类型
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        //设置appId
        String appId = "d15d52db-85d4-4216-bed6-48a6bb6bf1dd";
        headers.set("appId",appId);
        //设置时间戳
        String ts = String.valueOf(new Date().getTime());
        headers.set("ts", ts);

        //秘钥
        String secretKey = "205c3944-8699-4a3d-877a-215e9eb192c7";
        //设置签名
        headers.set("sign", this.MD5(appId + ts + secretKey));

        String tempFileName = file.getName();
        String tempFilePath = file.getAbsolutePath();

        headers.setContentType(type);
        String cd = "filename=\"" + tempFileName + "\"";
        headers.add("Content-Disposition", cd);

        // 封装参数
        FileSystemResource fileSystemResource = new FileSystemResource(tempFilePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
        form.add("file", fileSystemResource);//文件
        form.add("fileType", "项目立项报告");//文件类型
        form.add("projSetName", "测试项目群");//立项名称
        form.add("intrProjSetId", "G1000");//立项编号
        form.add("fileName", tempFileName);//文件名称
        form.add("opUserName","XXX");//操作人名称
        form.add("opUserAccount", "XXX.XX");//操作人账号
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        ResponseEntity<APIResponse> responseEntity = restTemplate.postForEntity(url, files, APIResponse.class);
        return responseEntity.getBody();
    }

    @ApiOperation("读取pmc中的相关信息")
    @GetMapping("/read")
    public APIResponse read(){
        String url = "http://127.0.0.1:8080/intf/projset/items";
        //-------设置请求头------
        HttpHeaders headers = new HttpHeaders();
        //设置请求类型
        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded");
        headers.setContentType(type);
        //设置appId
        String appId = "d15d52db-85d4-4216-bed6-48a6bb6bf1dd";
        headers.set("appId",appId);
        //设置时间戳
        String ts = String.valueOf(new Date().getTime());
        headers.set("ts", ts);
        //秘钥
        String secretKey = "205c3944-8699-4a3d-877a-215e9eb192c7";
        //设置签名
        headers.set("sign", this.MD5(appId + ts + secretKey));

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, APIResponse.class).getBody();
    }



    @ExceptionHandler(RestClientResponseException.class)
    public ErrorBody exceptionHandler(HttpClientErrorException e) throws IOException {
        return new ObjectMapper().readValue(e.getResponseBodyAsString(), ErrorBody.class);
    }

    private HttpEntity getJsonHttpRequest(Object dataObj){
        HttpHeaders headers =new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity request = new HttpEntity(dataObj, headers);
        return request;
    }

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
