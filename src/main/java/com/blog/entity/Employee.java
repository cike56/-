package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 1. 员工实体类
 *
 *
 * 1. FieldFill:枚举类
 *         `DEFAULT`为默认值，表示不填充
 *         `INSERT`表示插入时填充
 *         `UPDATE`表示修改时填充
 *         `INSERT_UPDATE`表示插入和修改时填充
 *
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    /*
    *           `DEFAULT`为默认值，表示不填充
     *         `INSERT`表示插入时填充
     *         `UPDATE`表示修改时填充
     *         `INSERT_UPDATE`表示插入和修改时填充
    * */

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
