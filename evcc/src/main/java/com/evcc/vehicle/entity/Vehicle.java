package com.evcc.vehicle.entity;

// import com.evcc.group.entity.Group; // Sẽ import sau khi tạo module 'group'
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate; // Biển số xe (VD: 51G-12345)

    @Column(nullable = false)
    private String model; // Tên xe (VD: Vinfast VF8)

    @Column(unique = true)
    private String vinNumber; // Số VIN (Số khung)

    private int modelYear; // Năm sản xuất

    private double batteryCapacityKWh; // Dung lượng pin (kWh)

    // --- Mối quan hệ với Group (Nhóm sở hữu) ---
    // Một xe sẽ thuộc về một nhóm.
    // Chúng ta sẽ tạm thời comment lại cho đến khi tạo 'Group' entity.
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "owner_group_id")
    // private Group ownerGroup;

}