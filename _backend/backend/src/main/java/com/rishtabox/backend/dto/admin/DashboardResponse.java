package com.rishtabox.backend.dto.admin;

public class DashboardResponse {

    private long totalProducts;
    private long totalUsers;
    private long totalOrders;
    private double totalSales;
    private long pendingOrders;
    private long completedOrders;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalProducts,
            long totalUsers,
            long totalOrders,
            double totalSales,
            long pendingOrders,
            long completedOrders) {

        this.totalProducts = totalProducts;
        this.totalUsers = totalUsers;
        this.totalOrders = totalOrders;
        this.totalSales = totalSales;
        this.pendingOrders = pendingOrders;
        this.completedOrders = completedOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }
}