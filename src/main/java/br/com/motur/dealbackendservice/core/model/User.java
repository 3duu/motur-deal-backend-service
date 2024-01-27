package br.com.motur.dealbackendservice.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Essa classe representa o um usuário do sistema.
 */
@Entity
@Table(name = "Users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "cognito_user_id")
    private String cognitoUserId;

    @Column(name = "dealer_id")
    private Long dealerId;

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false)
    private String email;

    private String name;

    private String role;

    @Column(nullable = false, name = "created_at")
    private Date createdAt;

    @Column(nullable = true, name = "updated_at")
    private Date updatedAt;

}
