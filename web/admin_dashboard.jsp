<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Dashboard Admin - AutoPro</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: Arial, sans-serif;
            }

            body {
                display: flex;
                background: #f1f2f6;
                color: #1f2937;
            }

            .sidebar {
                width: 260px;
                height: 100vh;
                position: sticky;
                top: 0;
                background: #111827;
                color: white;
                padding-top: 22px;
                overflow-y: auto;
            }

            .logo {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-bottom: 30px;
                padding-inline: 12px;
                min-height: 64px;
                overflow: visible;
                text-decoration: none;
            }

            .logo img {
                display: block;
                height: 46px;
                width: auto;
                max-width: 100%;
            }

            .menu a {
                display: block;
                color: #d1d5db;
                text-decoration: none;
                padding: 16px 25px;
                transition: 0.2s;
            }

            .menu a:hover,
            .menu a.active {
                background: #1f2937;
                color: white;
            }

            .logout {
                color: #fb7185 !important;
            }

            .main {
                flex: 1;
                padding: 24px 30px;
            }

            .topbar {
                background: white;
                padding: 22px;
                border-radius: 14px;
                margin-bottom: 24px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
            }

            .topbar h2 {
                margin-bottom: 6px;
            }

            .cards {
                display: grid;
                grid-template-columns: repeat(4, minmax(0, 1fr));
                gap: 18px;
                margin-bottom: 24px;
            }

            .card {
                background: white;
                padding: 24px;
                border-radius: 16px;
                box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
            }

            .card h4 {
                color: #64748b;
                margin-bottom: 12px;
                font-size: 14px;
                font-weight: 700;
                text-transform: uppercase;
                letter-spacing: 0.04em;
            }

            .card .value {
                font-size: 30px;
                font-weight: 700;
                color: #111827;
            }

            .card .hint {
                margin-top: 8px;
                color: #64748b;
                font-size: 13px;
            }

            .card.profit-card {
                background: linear-gradient(135deg, #ecfdf5 0%, #ffffff 58%);
                border: 1px solid #bbf7d0;
            }

            .card.profit-card h4 {
                color: #047857;
            }

            .card.profit-card .value {
                color: #065f46;
            }

            .grid-2 {
                display: grid;
                grid-template-columns: 2fr 1fr;
                gap: 18px;
                margin-bottom: 24px;
            }

            .chart-card,
            .recent-card {
                background: white;
                border-radius: 16px;
                padding: 22px;
                box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
            }

            .chart-card h3,
            .recent-card h3 {
                margin-bottom: 16px;
            }

            .chart-wrap {
                position: relative;
                height: 320px;
            }

            .chart-wrap.small {
                height: 280px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th,
            td {
                padding: 12px 10px;
                border-bottom: 1px solid #e5e7eb;
                text-align: left;
                font-size: 14px;
            }

            th {
                color: #64748b;
                font-size: 12px;
                text-transform: uppercase;
            }

            .empty {
                color: #64748b;
                text-align: center;
                padding: 26px 0;
            }

            .profit-section {
                margin-bottom: 24px;
            }

            .profit-section-banner {
                background: linear-gradient(135deg, #065f46 0%, #047857 50%, #10b981 100%);
                border-radius: 16px;
                padding: 22px 28px;
                margin-bottom: 18px;
                display: flex;
                align-items: center;
                gap: 14px;
                box-shadow: 0 4px 18px rgba(4, 120, 87, 0.18);
            }

            .profit-section-mark {
                width: 42px;
                height: 42px;
                border-radius: 14px;
                background: rgba(255, 255, 255, 0.18);
                position: relative;
                flex: 0 0 auto;
            }

            .profit-section-mark::before,
            .profit-section-mark::after {
                content: "";
                position: absolute;
                left: 12px;
                right: 12px;
                border-radius: 999px;
                background: #ffffff;
            }

            .profit-section-mark::before {
                height: 4px;
                bottom: 12px;
                box-shadow: 0 -8px 0 rgba(255, 255, 255, 0.72), 0 -16px 0 rgba(255, 255, 255, 0.42);
            }

            .profit-section-mark::after {
                width: 4px;
                height: 18px;
                left: 20px;
                bottom: 12px;
                background: #bbf7d0;
            }

            .profit-section-banner h3 {
                color: white;
                margin-bottom: 4px;
                font-size: 20px;
            }

            .profit-section-banner p {
                color: #bbf7d0;
                font-size: 13px;
                margin: 0;
            }

            .text-end-cell {
                text-align: right;
            }

            .transaction-link {
                color: #2563eb;
                text-decoration: none;
                font-weight: 600;
            }

            .value-strong {
                font-weight: 600;
            }

            .profit-value {
                color: #047857;
                font-weight: 700;
            }

            @media (max-width: 1280px) {
                .cards {
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                }

                .grid-2 {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 860px) {
                body {
                    display: block;
                }

                .sidebar {
                    width: 100%;
                    min-height: auto;
                }

                .cards {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <a class="logo" href="${pageContext.request.contextPath}/home">
                <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                    <jsp:param name="logoHeight" value="46px" />
                    <jsp:param name="onDark" value="1" />
                </jsp:include>
            </a>
            <div class="menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/users">Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/admin/posts">Quản lý bài đăng & xe</a>
                <a href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục</a>
                <a href="${pageContext.request.contextPath}/admin/reports">Báo cáo bài đăng</a>
                <a href="${pageContext.request.contextPath}/admin/lich-hen">Quản lý lịch hẹn</a>
                <a href="${pageContext.request.contextPath}/admin/giao-dich">Quản lý giao dịch xe</a>
                <a href="${pageContext.request.contextPath}/home">Xem trang chủ</a>
                <a class="logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>

        <div class="main">
            <div class="topbar">
                <div>
                    <h2>Dashboard tổng quan</h2>
                    <div class="text-muted">Theo dõi hoạt động kết nối giữa người mua và người bán, tiến độ duyệt bài, báo cáo vi phạm và lịch hẹn xem xe trên một màn hình.</div>
                </div>
                <div><strong>Admin:</strong> ${sessionScope.user.hoTen}</div>
            </div>

            <div class="cards">
                <div class="card">
                    <h4>Người dùng</h4>
                    <div class="value">${tongNguoiDung}</div>
                    <div class="hint">Tổng tài khoản hiện có trong hệ thống</div>
                </div>
                <div class="card">
                    <h4>Xe công khai</h4>
                    <div class="value">${tongBaiDangDaDuyet}</div>
                    <div class="hint">${tongXe} bản ghi xe trong kho dữ liệu</div>
                </div>
                <div class="card">
                    <h4>Bài chờ duyệt</h4>
                    <div class="value">${tongBaiDangChoDuyet}</div>
                    <div class="hint">Ưu tiên xử lý để tránh tồn backlog</div>
                </div>
                <div class="card">
                    <h4>Báo cáo chờ xử lý</h4>
                    <div class="value">${tongBaoCaoChoXuLy}</div>
                    <div class="hint">Theo dõi các bài bị người dùng tố cáo vi phạm</div>
                </div>
                <div class="card">
                    <h4>Yêu cầu hẹn xem xe</h4>
                    <div class="value">${tongYeuCauKetNoi}</div>
                    <div class="hint">${yeuCauDangXuLy} yêu cầu đang trong tiến trình</div>
                </div>
                <div class="card">
                    <h4>Hồ sơ giao dịch</h4>
                    <div class="value">${tongGiaoDichXe}</div>
                    <div class="hint">Tổng số giao dịch đã được tạo từ các yêu cầu mua xe</div>
                </div>
                <div class="card">
                    <h4>Chờ xác nhận</h4>
                    <div class="value">${giaoDichChoXacNhan}</div>
                    <div class="hint">Các giao dịch đang chờ một hoặc cả hai bên xác nhận</div>
                </div>
                <div class="card">
                    <h4>Giao dịch hoàn tất</h4>
                    <div class="value">${giaoDichHoanTat}</div>
                    <div class="hint">Đã được cả người mua và người bán xác nhận hoàn tất</div>
                </div>
                <div class="card">
                    <h4>Tổng giá trị tham khảo</h4>
                    <div class="value"><fmt:formatNumber value="${tongGiaTriGiaoDich}" type="number" maxFractionDigits="0"/> đ</div>
                    <div class="hint">Tổng giá trị của các giao dịch đã hoàn tất</div>
                </div>
                <div class="card profit-card">
                    <h4>Lợi nhuận ước tính</h4>
                    <div class="value"><fmt:formatNumber value="${loiNhuanUocTinh}" type="number" maxFractionDigits="0"/> đ</div>
                    <div class="hint">Tính theo phí nền tảng <fmt:formatNumber value="${tyLePhiNenTangPhanTram}" type="number" maxFractionDigits="2"/>% trên giao dịch hoàn tất</div>
                </div>
                <div class="card profit-card">
                    <h4>Lợi nhuận tháng này</h4>
                    <div class="value"><fmt:formatNumber value="${loiNhuanThangNay}" type="number" maxFractionDigits="0"/> đ</div>
                    <div class="hint">Ước tính từ các giao dịch hoàn tất trong tháng hiện tại</div>
                </div>
                <div class="card profit-card">
                    <h4>TB mỗi giao dịch</h4>
                    <div class="value"><fmt:formatNumber value="${loiNhuanTrungBinh}" type="number" maxFractionDigits="0"/> đ</div>
                    <div class="hint">Lợi nhuận trung bình trên mỗi giao dịch hoàn tất</div>
                </div>
                <div class="card">
                    <h4>Tăng trưởng tháng</h4>
                    <div class="value" style="color: ${tangTruongLoiNhuan >= 0 ? '#16a34a' : '#dc2626'}">
                        <c:if test="${tangTruongLoiNhuan >= 0}">▲ </c:if>
                        <c:if test="${tangTruongLoiNhuan < 0}">▼ </c:if>
                        <fmt:formatNumber value="${tangTruongLoiNhuan}" type="number" maxFractionDigits="1"/>%
                    </div>
                    <div class="hint">So với lợi nhuận tháng trước</div>
                </div>
                <div class="card">
                    <h4>Tranh chấp</h4>
                    <div class="value">${giaoDichTranhChap}</div>
                    <div class="hint">Những giao dịch admin cần xử lý thủ công</div>
                </div>
            </div>

            <div class="grid-2">
                <div class="chart-card">
                    <h3>Giao dịch, giá trị và lợi nhuận ước tính 6 tháng gần đây</h3>
                    <div class="chart-wrap">
                        <canvas id="salesRevenueChart"></canvas>
                    </div>
                </div>
                <div class="chart-card">
                    <h3>Phân bố trạng thái giao dịch</h3>
                    <div class="chart-wrap small">
                        <canvas id="statusChart"></canvas>
                    </div>
                </div>
            </div>

            <div class="grid-2">
                <div class="chart-card">
                    <h3>Top tỉnh/thành có xe đang hiển thị</h3>
                    <div class="chart-wrap small">
                        <canvas id="provinceChart"></canvas>
                    </div>
                </div>
                <div class="recent-card">
                    <h3>Bài đăng chờ duyệt gần đây</h3>
                    <c:choose>
                        <c:when test="${not empty recentPosts}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Bài đăng</th>
                                        <th>Xe</th>
                                        <th>Giá</th>
                                        <th>Ngày đăng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentPosts}" var="post">
                                        <tr>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/post-detail?id=${post.maBaiDang}" class="text-decoration-none">
                                                    ${post.tieuDe}
                                                </a>
                                            </td>
                                            <td>${post.tenXe}</td>
                                            <td><fmt:formatNumber value="${post.giaBan}" type="number" maxFractionDigits="0"/> đ</td>
                                            <td><fmt:formatDate value="${post.ngayDang}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="empty">Hiện chưa có bài đăng nào chờ duyệt.</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- PHẦN THỐNG KÊ LỢI NHUẬN CHI TIẾT -->
            <div class="profit-section">
                <div class="profit-section-banner">
                    <span class="profit-section-mark" aria-hidden="true"></span>
                    <div>
                        <h3>Thống kê lợi nhuận chi tiết</h3>
                        <p>Phân tích lợi nhuận ước tính theo ngày, theo hãng xe và bảng chi tiết giao dịch hoàn tất gần đây</p>
                    </div>
                </div>

                <div class="grid-2" style="margin-bottom:18px">
                    <div class="chart-card">
                        <h3>Lợi nhuận theo ngày (tháng hiện tại)</h3>
                        <div class="chart-wrap">
                            <canvas id="dailyProfitChart"></canvas>
                        </div>
                    </div>
                    <div class="chart-card">
                        <h3>Lợi nhuận theo hãng xe</h3>
                        <div class="chart-wrap small">
                            <canvas id="brandProfitChart"></canvas>
                        </div>
                    </div>
                </div>

                <div class="chart-card">
                    <h3 style="margin-bottom:16px">Giao dịch hoàn tất gần đây</h3>
                    <div style="overflow-x:auto">
                        <table id="recentCompletedTable">
                            <thead>
                                <tr>
                                    <th>Mã GD</th>
                                    <th>Xe</th>
                                    <th>Người mua</th>
                                    <th>Người bán</th>
                                    <th class="text-end-cell">Giá trị GD</th>
                                    <th class="text-end-cell">Lợi nhuận ước tính</th>
                                    <th>Ngày hoàn tất</th>
                                </tr>
                            </thead>
                            <tbody id="recentCompletedBody"></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <script>
            const monthlyStats = ${monthlyStatsJson};
            const statusSummary = ${statusSummaryJson};
            const provinceSummary = ${provinceSummaryJson};

            const monthlyLabels = monthlyStats.map(item => item.label);
            const monthlySales = monthlyStats.map(item => item.sales);
            const monthlyRevenue = monthlyStats.map(item => item.revenue);
            const monthlyProfit = monthlyStats.map(item => item.profit);

            new Chart(document.getElementById('salesRevenueChart'), {
                type: 'bar',
                data: {
                    labels: monthlyLabels,
                    datasets: [
                        {
                            type: 'line',
                            label: 'Số giao dịch hoàn tất',
                            data: monthlySales,
                            borderColor: '#2563eb',
                            backgroundColor: '#2563eb',
                            yAxisID: 'ySales',
                            tension: 0.3
                        },
                        {
                            label: 'Tổng giá trị giao dịch',
                            data: monthlyRevenue,
                            backgroundColor: '#f59e0b',
                            borderRadius: 8,
                            yAxisID: 'yRevenue'
                        },
                        {
                            type: 'line',
                            label: 'Lợi nhuận ước tính',
                            data: monthlyProfit,
                            borderColor: '#16a34a',
                            backgroundColor: '#16a34a',
                            yAxisID: 'yProfit',
                            tension: 0.3
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    interaction: {
                        mode: 'index',
                        intersect: false
                    },
                    scales: {
                        ySales: {
                            type: 'linear',
                            position: 'left',
                            beginAtZero: true,
                            ticks: {
                                precision: 0
                            }
                        },
                        yRevenue: {
                            type: 'linear',
                            position: 'right',
                            beginAtZero: true,
                            grid: {
                                drawOnChartArea: false
                            }
                        },
                        yProfit: {
                            type: 'linear',
                            position: 'right',
                            beginAtZero: true,
                            grid: {
                                drawOnChartArea: false
                            }
                        }
                    }
                }
            });

            new Chart(document.getElementById('statusChart'), {
                type: 'doughnut',
                data: {
                    labels: statusSummary.map(item => item.label),
                    datasets: [{
                        data: statusSummary.map(item => item.count),
                        backgroundColor: ['#fbbf24', '#60a5fa', '#fb923c', '#4ade80', '#f87171', '#a78bfa']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });

            new Chart(document.getElementById('provinceChart'), {
                type: 'bar',
                data: {
                    labels: provinceSummary.map(item => item.label),
                    datasets: [{
                        label: 'Số xe đang hiển thị',
                        data: provinceSummary.map(item => item.count),
                        backgroundColor: '#14b8a6',
                        borderRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                precision: 0
                            }
                        }
                    }
                }
            });

            // === BIỂU ĐỒ LỢI NHUẬN THEO NGÀY ===
            const dailyProfit = ${dailyProfitJson};
            const dailyCtx = document.getElementById('dailyProfitChart').getContext('2d');
            const dailyGradient = dailyCtx.createLinearGradient(0, 0, 0, 320);
            dailyGradient.addColorStop(0, 'rgba(16, 185, 129, 0.35)');
            dailyGradient.addColorStop(1, 'rgba(16, 185, 129, 0.02)');

            new Chart(dailyCtx, {
                type: 'line',
                data: {
                    labels: dailyProfit.map(item => item.label),
                    datasets: [{
                        label: 'Lợi nhuận ước tính',
                        data: dailyProfit.map(item => item.profit),
                        borderColor: '#10b981',
                        backgroundColor: dailyGradient,
                        fill: true,
                        tension: 0.35,
                        pointRadius: 3,
                        pointBackgroundColor: '#10b981',
                        pointHoverRadius: 6,
                        borderWidth: 2.5
                    }, {
                        type: 'bar',
                        label: 'Số giao dịch',
                        data: dailyProfit.map(item => item.count),
                        backgroundColor: 'rgba(37, 99, 235, 0.18)',
                        borderColor: '#2563eb',
                        borderWidth: 1,
                        borderRadius: 4,
                        yAxisID: 'yCount'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    interaction: { mode: 'index', intersect: false },
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    if (context.dataset.label === 'Lợi nhuận ước tính') {
                                        return context.dataset.label + ': ' + new Intl.NumberFormat('vi-VN').format(Math.round(context.raw)) + ' đ';
                                    }
                                    return context.dataset.label + ': ' + context.raw;
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    if (value >= 1000000) return (value / 1000000).toFixed(1) + 'tr';
                                    if (value >= 1000) return (value / 1000).toFixed(0) + 'k';
                                    return value;
                                }
                            }
                        },
                        yCount: {
                            type: 'linear',
                            position: 'right',
                            beginAtZero: true,
                            grid: { drawOnChartArea: false },
                            ticks: { precision: 0 }
                        }
                    }
                }
            });

            // === BIỂU ĐỒ LỢI NHUẬN THEO HÃNG XE ===
            const profitByBrand = ${profitByBrandJson};
            const brandColors = ['#10b981', '#059669', '#047857', '#065f46', '#14b8a6', '#0d9488', '#0f766e', '#134e4a'];

            new Chart(document.getElementById('brandProfitChart'), {
                type: 'bar',
                data: {
                    labels: profitByBrand.map(item => item.label),
                    datasets: [{
                        label: 'Lợi nhuận ước tính',
                        data: profitByBrand.map(item => item.profit),
                        backgroundColor: profitByBrand.map((_, i) => brandColors[i % brandColors.length]),
                        borderRadius: 6,
                        barThickness: 22
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const item = profitByBrand[context.dataIndex];
                                    return [
                                        'Lợi nhuận: ' + new Intl.NumberFormat('vi-VN').format(Math.round(context.raw)) + ' đ',
                                        'Tổng giá trị: ' + new Intl.NumberFormat('vi-VN').format(Math.round(item.revenue)) + ' đ',
                                        'Số GD: ' + item.count
                                    ];
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    if (value >= 1000000) return (value / 1000000).toFixed(1) + 'tr';
                                    if (value >= 1000) return (value / 1000).toFixed(0) + 'k';
                                    return value;
                                }
                            }
                        }
                    }
                }
            });

            // === BẢNG GIAO DỊCH HOÀN TẤT GẦN ĐÂY ===
            const recentCompleted = ${recentCompletedJson};
            const tbody = document.getElementById('recentCompletedBody');
            const adminTransactionBaseUrl = '${pageContext.request.contextPath}/admin/giao-dich?id=';

            function formatVND(value) {
                const amount = Number(value || 0);
                return new Intl.NumberFormat('vi-VN').format(Math.round(amount)) + ' đ';
            }

            function appendTextCell(row, text, className) {
                const cell = document.createElement('td');
                if (className) {
                    cell.className = className;
                }
                cell.textContent = text || '-';
                row.appendChild(cell);
                return cell;
            }

            if (recentCompleted.length === 0) {
                const tr = document.createElement('tr');
                const td = document.createElement('td');
                td.colSpan = 7;
                td.className = 'empty';
                td.textContent = 'Chưa có giao dịch hoàn tất nào.';
                tr.appendChild(td);
                tbody.appendChild(tr);
            } else {
                recentCompleted.forEach(function(item) {
                    const tr = document.createElement('tr');
                    const idCell = document.createElement('td');
                    const idLink = document.createElement('a');
                    idLink.href = adminTransactionBaseUrl + encodeURIComponent(item.maGiaoDich || '');
                    idLink.className = 'transaction-link';
                    idLink.textContent = '#' + (item.maGiaoDich || '-');
                    idCell.appendChild(idLink);
                    tr.appendChild(idCell);

                    appendTextCell(tr, item.tenXe);
                    appendTextCell(tr, item.tenNguoiMua);
                    appendTextCell(tr, item.tenNguoiBan);
                    appendTextCell(tr, formatVND(item.giaTriGiaoDich), 'text-end-cell value-strong');
                    appendTextCell(tr, formatVND(item.loiNhuan), 'text-end-cell profit-value');
                    appendTextCell(tr, item.ngayHoanTat);
                    tbody.appendChild(tr);
                });
            }
        </script>
    </body>
</html>
