package com.evcc.document.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ContractCreateRequest {
    private String title;
    private Long templateId; // ID của "mẫu giấy tờ sẵn"
    // Dữ liệu form (ví dụ: "ten_nguoi_a": "Nguyễn Văn A", "bien_so_xe": "51G-12345")
    private Map<String, String> formData; 
}