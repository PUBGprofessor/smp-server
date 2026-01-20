package com.pubg.smp.smpinfo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.pubg.smp.smpinfo.dto.StudentUserDTO;
import com.pubg.smp.smpinfo.entity.RestModel;
import com.pubg.smp.smpinfo.entity.User;
import com.pubg.smp.smpinfo.security.LoginUser;
import com.pubg.smp.smpinfo.security.MustCounselorLogin;
import com.pubg.smp.smpinfo.service.UserService;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * 用户控制层
 *
 * @author itning
 */
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/debug-all")
    public void debug(HttpServletRequest request) {
        System.out.println("=========  开始调试请求信息 =========");

        // 1. 打印所有【参数】 (Query Params / Form Data)
        System.out.println("--- [Parameters] ---");
        Enumeration<String> parameterNames = request.getParameterNames();
        if (!parameterNames.hasMoreElements()) {
            System.out.println(" (无参数)");
        }
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            // getParameterValues 可以获取同名多个参数的情况
            String value = request.getParameter(name);
            System.out.println(name + " : " + value);
        }

        // 2. 打印所有【请求头】 (Headers)
        System.out.println("\n--- [Headers] ---");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + " : " + request.getHeader(name));
        }

        System.out.println("========= 调试结束 =========");
    }

    /**
     * 分页获取学生信息
     *
     * @param pageable 分页信息
     * @return RestModel
     */
    @GetMapping("/users")

    public ResponseEntity<?> getAllUserInfo(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                                    Pageable pageable,
                                            @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(userService.getAllUser(pageable, loginUser));
    }

    /**
     * 搜索用户
     *
     * @param key      用户名
     * @param pageable 分页信息
     * @return RestModel
     */
    @GetMapping("/search/users/{key}")
    public ResponseEntity<?> searchUsers(@PathVariable String key,
                                         @PageableDefault(size = 20, sort = {"gmtModified"},
                                                 direction = Sort.Direction.DESC)
                                                 Pageable pageable,
                                         @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(userService.searchUsers(key, pageable, loginUser));
    }

    /**
     * 更新用户信息
     *
     * @param studentUserDTO 用户信息
     * @return ResponseEntity
     */
    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody StudentUserDTO studentUserDTO,
                                        @MustCounselorLogin LoginUser loginUser) {
        userService.updateUser(studentUserDTO, loginUser);
        return RestModel.noContent();
    }

    /**
     * 删除用户
     *
     * @param id ID
     * @return ResponseEntity
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delUser(@PathVariable String id,
                                     @MustCounselorLogin LoginUser loginUser) {
        userService.delUser(id, loginUser);
        return RestModel.noContent();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传
     */
    @PostMapping("/user/file")
    public ResponseEntity<?> newUser(@RequestParam("file") MultipartFile file,
                                     @MustCounselorLogin LoginUser loginUser) throws IOException {
        return RestModel.created(userService.upFile(file, loginUser));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/internal/user/{username}")
    public User getUserInfoByUserName(@PathVariable String username) {
        return userService.getUserInfoByUserName(username);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/internal/student_user/{username}")
    public StudentUserDTO getStudentUserDtoFromUserName(@PathVariable String username) {
        return userService.getStudentUserInfoByUserName(username);
    }

    /**
     * 计算学生人数
     *
     * @param username 导员用户名
     * @return 学生数量
     */
    @GetMapping("/internal/student_user/count/{username}")
    public long countStudent(@PathVariable String username) {
        return userService.countStudent(username);
    }

    /**
     * 获取所有学生信息
     *
     * @param username 导员用户名
     * @return 学生信息
     */
    @GetMapping("/internal/users")
    public List<StudentUserDTO> getAllUser(@RequestParam String username) {
        return userService.getAllUser(username);
    }

    /**
     * 获取学生信息根据学号
     *
     * @param studentId 学号
     * @return 学生信息
     */
    @GetMapping("/internal/student_user_id/{studentId}")
    public StudentUserDTO getStudentUserDtoByStudentId(@PathVariable String studentId) {
        return userService.getStudentUserDtoByStudentId(studentId);
    }

    /**
     * 获取所有辅导员用户
     *
     * @return 用户
     */
    @GetMapping("/internal/counselor/users")
    public List<User> getAllCounselorUser() {
        return userService.getAllCounselorUser();
    }

    /**
     * 获取所有辅导员用户
     *
     * @return 用户
     */
    @PostMapping("/internal/pwd/user/change")
    public boolean changeUserPwd(@RequestParam String username, @RequestParam String newPwd) {
        return userService.changeUserPwd(username, newPwd);
    }
}
