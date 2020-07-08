package com.example.spms.check.entity;

import java.io.Serializable;
import java.util.List;

public class CheckJobs implements Serializable {

    private List<ApplicantOrderListBean> applicantOrderList;

    public List<ApplicantOrderListBean> getApplicantOrderList() {
        return applicantOrderList;
    }

    public void setApplicantOrderList(List<ApplicantOrderListBean> applicantOrderList) {
        this.applicantOrderList = applicantOrderList;
    }

    public static class ApplicantOrderListBean {
        /**
         * checkOrder : {"checkOrderId":"gana20200612"}
         * checkOrderDetails : [{"materialId":213345889,"requestNumber":123,"actualNumber":-1},{"materialId":216694867,"requestNumber":1399,"actualNumber":-1}]
         */

        private CheckOrderBean checkOrder;
        private List<CheckOrderDetailsBean> checkOrderDetails;

        public CheckOrderBean getCheckOrder() {
            return checkOrder;
        }

        public void setCheckOrder(CheckOrderBean checkOrder) {
            this.checkOrder = checkOrder;
        }

        public List<CheckOrderDetailsBean> getCheckOrderDetails() {
            return checkOrderDetails;
        }

        public void setCheckOrderDetails(List<CheckOrderDetailsBean> checkOrderDetails) {
            this.checkOrderDetails = checkOrderDetails;
        }

        public static class CheckOrderBean {
            /**
             * checkOrderId : gana20200612
             */

            private String checkOrderId;

            public String getCheckOrderId() {
                return checkOrderId;
            }

            public void setCheckOrderId(String checkOrderId) {
                this.checkOrderId = checkOrderId;
            }
        }

        public static class CheckOrderDetailsBean {
            /**
             * materialId : 213345889
             * requestNumber : 123
             * actualNumber : -1
             */

            private int materialId;
            private int requestNumber;
            private int actualNumber;

            public int getMaterialId() {
                return materialId;
            }

            public void setMaterialId(int materialId) {
                this.materialId = materialId;
            }

            public int getRequestNumber() {
                return requestNumber;
            }

            public void setRequestNumber(int requestNumber) {
                this.requestNumber = requestNumber;
            }

            public int getActualNumber() {
                return actualNumber;
            }

            public void setActualNumber(int actualNumber) {
                this.actualNumber = actualNumber;
            }
        }
    }
}
