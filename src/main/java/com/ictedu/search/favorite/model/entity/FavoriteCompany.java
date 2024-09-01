package com.ictedu.search.favorite.model.entity;

import com.ictedu.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorite_company")
public class FavoriteCompany {

    @Id
    @SequenceGenerator(name="seq_favorite_id", sequenceName = "seq_favorite_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;
}
