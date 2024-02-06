package org.dnd.timeet.common.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class AuditableEntity extends BaseEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 64)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 64)
    protected String lastModifiedBy = "";
}
