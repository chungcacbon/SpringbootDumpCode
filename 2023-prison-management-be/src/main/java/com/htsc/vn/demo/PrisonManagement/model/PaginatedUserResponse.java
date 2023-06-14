package com.htsc.vn.demo.PrisonManagement.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedUserResponse {
	private List<UserClearInfo> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
