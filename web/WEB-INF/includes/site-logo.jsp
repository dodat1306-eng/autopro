<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="resolvedHeight" value="${empty param.logoHeight ? '52px' : param.logoHeight}" />
<c:set var="isIconOnly" value="${param.iconOnly eq '1'}" />
<c:set var="isDarkBackground" value="${param.onDark eq '1'}" />
<c:set var="assetVersion" value="20260504-transparent" />
<c:choose>
    <c:when test="${isIconOnly}">
        <c:set var="logoSrc" value="${pageContext.request.contextPath}/logo/autopro_icon.png?v=${assetVersion}" />
    </c:when>
    <c:otherwise>
        <c:set var="logoSrc" value="${pageContext.request.contextPath}/logo/autopro_logo_transparent.png?v=${assetVersion}" />
    </c:otherwise>
</c:choose>
<span class="logo-link" style="display:flex;align-items:center;text-decoration:none;overflow:visible;line-height:1;flex:0 0 auto;min-width:0;">
    <img src="${logoSrc}"
         alt="AutoPro Logo"
         decoding="async"
         class="logo-img"
         style="height:${resolvedHeight};width:auto;object-fit:contain;display:block;image-rendering:auto;flex:0 0 auto;filter:${isDarkBackground ? 'drop-shadow(0 0 1px rgba(255,255,255,0.95)) drop-shadow(0 0 8px rgba(255,255,255,0.22))' : 'none'};">
</span>
