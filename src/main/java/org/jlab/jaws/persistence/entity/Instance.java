package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "INSTANCE", schema = "JAWS_OWNER")
public class Instance implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "InstanceId", sequenceName = "INSTANCE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InstanceId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "INSTANCE_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger instanceId;
    @Size(max = 64)
    @Column(length = 64)
    private String name;
    @JoinColumn(name = "CLASS_ID", referencedColumnName = "CLASS_ID", nullable = false)
    @ManyToOne(optional = false)
    private AlarmClass alarmClass;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "INSTANCE_LOCATION", joinColumns = @JoinColumn(name = "INSTANCE_ID"), inverseJoinColumns = @JoinColumn(name = "LOCATION_ID"))
    private List<Location> locationList;

    public BigInteger getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(BigInteger instanceId) {
        this.instanceId = instanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AlarmClass getAlarmClass() {
        return alarmClass;
    }

    public void setAlarmClass(AlarmClass alarmClass) {
        this.alarmClass = alarmClass;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public String getLocationNameCsv() {
        String csv = "";

        if(locationList != null && !locationList.isEmpty()) {
            csv = locationList.get(0).getName();

            for(int i = 1; i < locationList.size(); i++) {
                csv = ", " + locationList.get(i).getName();
            }
        }

        return csv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;
        Instance entity = (Instance) o;
        return Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}