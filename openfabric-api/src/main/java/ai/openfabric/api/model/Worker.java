package ai.openfabric.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity()
@Table(name="worker")
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public String image;

    @Getter
    @Setter
    public String host_name;

    @Getter
    @Setter
    public String cmd;

    @Getter
    @Setter
    public String env;

    @Getter
    @Setter
    public String ports;

    @Getter
    @Setter
    public String binds;

    @Getter
    @Setter
    public String container_id;

    @Getter
    @Setter
    public Integer status;

}
