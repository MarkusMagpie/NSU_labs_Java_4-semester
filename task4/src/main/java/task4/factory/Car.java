package task4.factory;

class Car extends Part{
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car(int id, Body body, Motor motor, Accessory accessory) {
        super(id);
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }

//    @Override
//    public String toString() {
//        return "Car: " + super.toString() + " (Body: " + body.getId() + ", Motor: " + motor.getId() + ", Accessory: "
//                + accessory.getId() + ")";
//    }

    public Body getBody() {
        return body;
    }

    public Motor getMotor() {
        return motor;
    }

    public Accessory getAccessory() {
        return accessory;
    }
}
