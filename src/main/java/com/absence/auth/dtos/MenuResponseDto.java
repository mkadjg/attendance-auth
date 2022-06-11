package com.absence.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponseDto {
    private String menuId;
    private String menuTitle;
    private String menuType;
    private String menuUrl;
    private String menuIcon;
    private String menuBreadcrumbs;
    private String menuTarget;
    private String menuExternal;
    private List<MenuResponseDto> children;
}
