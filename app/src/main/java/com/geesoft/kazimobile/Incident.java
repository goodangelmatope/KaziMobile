package com.geesoft.kazimobile;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Zamswitch on 30/06/2017.
 */

public class Incident implements Serializable {

    private int id;
    private int companyID;
    private int status;

    private String referenceNumber;
    private String customerName;
    private String siteName;
    private float siteGPSLatitude;
    private float siteGPSLongitude;
    private String incidentType;
    private String installationType;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private Date creationDate;
    private Date assignedDate;
    private String description;
    private String comments;
    private Date targetResolutionTime;
    private int assignedUserID;
    private boolean readByUser;
    private float readGPSLatitude;
    private float readGPSLongitude;
    private Date readTime;
    private Date travelStartTime;
    private Date arrivalTime;
    private Date jobStartTime;
    private float jobStartGPPSLatitude;
    private float jobStartGPSLongitude;
    private Date jobEndTime;
    private int resolutionType;
    private String resolutionComments;
    private boolean resolved;
    private float replenishmentQuantity;

    public Incident(int id, int companyID, int status, String referenceNumber, String customerName,
                    String siteName, float siteGPSLatitude, float siteGPSLongitude, String incidentType,
                    String installationType, String manufacturer, String model, String serialNumber,
                    Date creationDate, Date assignedDate, String description, String comments,
                    Date targetResolutionTime, int assignedUserID, boolean readByUser,
                    float readGPSLatitude, float readGPSLongitude, Date readTime,
                    Date travelStartTime, Date arrivalTime, Date jobStartTime, float jobStartGPPSLatitude,
                    float jobStartGPSLongitude, Date jobEndTime, int resolutionType, String resolutionComments,
                    boolean resolved, float replenishmentQuantity) {

                this.id = id;
                this.companyID = companyID;
                this.status = status;
                this.referenceNumber = referenceNumber;
                this.customerName = customerName;
                this.siteName = siteName;
                this.siteGPSLatitude = siteGPSLatitude;
                this.siteGPSLongitude = siteGPSLongitude;
                this.incidentType = incidentType;
                this.installationType = installationType;
                this.manufacturer = manufacturer;
                this.model = model;
                this.serialNumber = serialNumber;
                this.creationDate = creationDate;
                this.assignedDate = assignedDate;
                this.description = description;
                this.comments = comments;
                this.targetResolutionTime = targetResolutionTime;
                this.assignedUserID = assignedUserID;
                this.readByUser = readByUser;
                this.readGPSLatitude = readGPSLatitude;
                this.readGPSLongitude = readGPSLongitude;
                this.readTime = readTime;
                this.travelStartTime = travelStartTime;
                this.arrivalTime = arrivalTime;
                this.jobStartTime = jobStartTime;
                this.jobStartGPPSLatitude = jobStartGPPSLatitude;
                this.jobStartGPSLongitude = jobStartGPSLongitude;
                this.jobEndTime = jobEndTime;
                this.resolutionType = resolutionType;
                this.resolutionComments = resolutionComments;
                this.resolved = resolved;
                this.replenishmentQuantity = replenishmentQuantity;
    }

    public float getSiteGPSLatitude() {
        return siteGPSLatitude;
    }

    public void setSiteGPSLatituee(float siteGPSLatitude) {
        this.siteGPSLatitude = siteGPSLatitude;
    }

    public float getSiteGPSLongitude() {
        return siteGPSLongitude;
    }

    public void setSiteGPSLongitude(float siteGPSLongitude) {
        this.siteGPSLongitude = siteGPSLongitude;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getInstallationType() {
        return installationType;
    }

    public void setInstallationType(String installationType) {
        this.installationType = installationType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getTargetResolutionTime() {
        return targetResolutionTime;
    }

    public void setTargetResolutionTime(Date targetResolutionTime) {
        this.targetResolutionTime = targetResolutionTime;
    }

    public int getAssignedUserID() {
        return assignedUserID;
    }

    public void setAssignedUserID(int assignedUserID) {
        this.assignedUserID = assignedUserID;
    }

    public boolean isReadByUser() {
        return readByUser;
    }

    public void setReadByUser(boolean readByUser) {
        this.readByUser = readByUser;
    }

    public float getReadGPSLatitude() {
        return readGPSLatitude;
    }

    public void setReadGPSLatitude(float readGPSLatitude) {
        this.readGPSLatitude = readGPSLatitude;
    }

    public float getReadGPSLongitude() {
        return readGPSLongitude;
    }

    public void setReadGPSLongitude(float readGPSLongitude) {
        this.readGPSLongitude = readGPSLongitude;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getTravelStartTime() {
        return travelStartTime;
    }

    public void setTravelStartTime(Date travelStartTime) {
        this.travelStartTime = travelStartTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public float getJobStartGPPSLatitude() {
        return jobStartGPPSLatitude;
    }

    public void setJobStartGPPSLatitude(float jobStartGPPSLatitude) {
        this.jobStartGPPSLatitude = jobStartGPPSLatitude;
    }

    public float getJobStartGPSLongitude() {
        return jobStartGPSLongitude;
    }

    public void setJobStartGPSLongitude(float jobStartGPSLongitude) {
        this.jobStartGPSLongitude = jobStartGPSLongitude;
    }

    public Date getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public int getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(int resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionComments() {
        return resolutionComments;
    }

    public void setResolutionComments(String resolutionComments) {
        this.resolutionComments = resolutionComments;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public float getReplenishmentQuantity() {
        return replenishmentQuantity;
    }

    public void setReplenishmentQuantity(float replenishmentQuantity) {
        this.replenishmentQuantity = replenishmentQuantity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
