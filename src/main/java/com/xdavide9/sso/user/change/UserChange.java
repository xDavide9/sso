package com.xdavide9.sso.user.change;

import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.fields.UserField;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a change made to any of the fields of a {@link User}.
 * It makes use of JpaAuditing to populate creationDate and createdBy field.
 * Every other field must be set when altering user objects.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Entity
@Table(name = "sso_user_change")
@EntityListeners(AuditingEntityListener.class)
public class UserChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "uuid", nullable = false, updatable = false)
    private User user;
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserField field;
    /**
     * It is nullable because of password (do not record encoded one)
     */
    @Column(updatable = false)
    private String previousValue;
    /**
     * It is nullable because of password (do not record encoded one)
     */
    @Column(updatable = false)
    private String updatedValue;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime creationDate;
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private UUID createdBy;

    public UserChange() {}

    public UserChange(User user,
                      UserField field,
                      String previousValue,
                      String updatedValue) {
        this.user = user;
        this.field = field;
        this.previousValue = previousValue;
        this.updatedValue = updatedValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserField getField() {
        return field;
    }

    public void setField(UserField field) {
        this.field = field;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getUpdatedValue() {
        return updatedValue;
    }

    public void setUpdatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserChange that = (UserChange) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(field, that.field) &&
                Objects.equals(previousValue, that.previousValue) &&
                Objects.equals(updatedValue, that.updatedValue) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, field, previousValue, updatedValue, creationDate, createdBy);
    }

    @Override
    public String toString() {
        return "UserChange{" +
                "id=" + id +
                ", user=" + user +
                ", field='" + field + '\'' +
                ", previousValue='" + previousValue + '\'' +
                ", updatedValue='" + updatedValue + '\'' +
                ", creationDate=" + creationDate +
                ", createdBy=" + createdBy +
                '}';
    }
}
