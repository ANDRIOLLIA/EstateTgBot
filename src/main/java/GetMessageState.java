public class GetMessageState {
    private boolean isCreateCustomer;
    private boolean isWaitingName;
    private boolean isWaitingPhone;
    private boolean isWaitingCity;
    private boolean isWaitingType;
    private boolean isWaitingId;
    private boolean isWaitingTime;

    public GetMessageState() {
        isCreateCustomer = false;
        isWaitingName = false;
        isWaitingPhone = false;
        isWaitingCity = false;
        isWaitingType = false;
        isWaitingId = false;
        isWaitingTime = false;
    }

    public boolean isCreateCustomer() {
        return isCreateCustomer;
    }

    public void setCreateCustomer(boolean createCustomer) {
        isCreateCustomer = createCustomer;
    }

    public boolean isWaitingName() {
        return isWaitingName;
    }

    public void setWaitingName(boolean waitingName) {
        isWaitingName = waitingName;
    }

    public boolean isWaitingPhone() {
        return isWaitingPhone;
    }

    public void setWaitingPhone(boolean waitingPhone) {
        isWaitingPhone = waitingPhone;
    }

    public boolean isWaitingCity() {
        return isWaitingCity;
    }

    public void setWaitingCity(boolean waitingCity) {
        isWaitingCity = waitingCity;
    }

    public boolean isWaitingType() {
        return isWaitingType;
    }

    public void setWaitingType(boolean waitingType) {
        isWaitingType = waitingType;
    }

    public boolean isWaitingId() {
        return isWaitingId;
    }

    public void setWaitingId(boolean waitingId) {
        isWaitingId = waitingId;
    }

    public boolean isWaitingTime() {
        return isWaitingTime;
    }

    public void setWaitingTime(boolean waitingTime) {
        isWaitingTime = waitingTime;
    }
}