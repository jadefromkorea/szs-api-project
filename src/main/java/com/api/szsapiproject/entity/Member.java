package com.api.szsapiproject.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Member {
    @Id
    /** user id */
    private String userId;
    /** user 비밀번호 */
    private String password;
    /** user 이름 */
    private String name;
    /** user 주민등록번호 */
    private String regNo;
}




