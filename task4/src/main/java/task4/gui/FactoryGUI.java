package task4.gui;

import task4.factory.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FactoryGUI extends JFrame {
    private final JSlider body_supplier_speed;
    private final JSlider motor_supplier_speed;
    private final JSlider accessory_supplier_speed;
    private final JSlider dealer_speed;

    private final JLabel body_storage_label;
    private final JLabel motor_storage_label;
    private final JLabel accessory_storage_label;
    private final JLabel car_storage_label;

    private final JLabel sold_cars_label;

    private final int body_storage_capacity;
    private final int motor_storage_capacity;
    private final int accessory_storage_capacity;
    private final int car_storage_capacity;

    private final List<Supplier<Body>> bodySuppliers; // Используем java.util.List
    private final List<Supplier<Motor>> motorSuppliers;
    private final List<Supplier<Accessory>> accessorySuppliers;
    private final List<Dealer> dealers;

    public FactoryGUI(List<Supplier<Body>> bodySuppliers, List<Supplier<Motor>> motorSuppliers,
                      List<Supplier<Accessory>> accessorySuppliers, List<Dealer> dealers,

                      int body_storage_capacity, int motor_storage_capacity,
                      int accessory_storage_capacity, int car_storage_capacity,

                      int body_supplier_delay, int motor_supplier_delay,
                      int accessory_supplier_delay, int dealer_delay) {
        this.body_storage_capacity = body_storage_capacity;
        this.motor_storage_capacity = motor_storage_capacity;
        this.accessory_storage_capacity = accessory_storage_capacity;
        this.car_storage_capacity = car_storage_capacity;

        this.bodySuppliers = bodySuppliers;
        this.motorSuppliers = motorSuppliers;
        this.accessorySuppliers = accessorySuppliers;
        this.dealers = dealers;

        // создание окна
        setTitle("Factory control panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLayout(new BorderLayout());

        // панель статистики заполнености складов
        JPanel stats_panel = new JPanel();
        stats_panel.setLayout(new GridLayout(6, 1));

        body_storage_label = new JLabel("Body storage: 0/" + body_storage_capacity);
        motor_storage_label = new JLabel("Motor storage: 0/" + motor_storage_capacity);
        accessory_storage_label = new JLabel("Accessory storage: 0/10" + accessory_storage_capacity);
        car_storage_label = new JLabel("Car storage: 0/10" + car_storage_capacity);

        sold_cars_label = new JLabel("Sold cars: 0");

        stats_panel.add(body_storage_label);
        stats_panel.add(motor_storage_label);
        stats_panel.add(accessory_storage_label);
        stats_panel.add(car_storage_label);
        stats_panel.add(sold_cars_label);

        add(stats_panel, BorderLayout.CENTER);

        JPanel control_panel = new JPanel();
        control_panel.setLayout(new GridLayout(4, 2));

        // ползунки скоростей
        JLabel bodySpeedLabel = new JLabel("Body supplier delay (ms):", JLabel.LEFT);
        body_supplier_speed = createSlider(100, 10000, body_supplier_delay);
        control_panel.add(bodySpeedLabel);
        control_panel.add(body_supplier_speed);

        JLabel motorSpeedLabel = new JLabel("Motor supplier delay (ms):", JLabel.LEFT);
        motor_supplier_speed = createSlider(100, 10000, motor_supplier_delay);
        control_panel.add(motorSpeedLabel);
        control_panel.add(motor_supplier_speed);

        JLabel accessorySpeedLabel = new JLabel("Accessory supplier delay (ms):", JLabel.LEFT);
        accessory_supplier_speed = createSlider(100, 10000, accessory_supplier_delay);
        control_panel.add(accessorySpeedLabel);
        control_panel.add(accessory_supplier_speed);

        JLabel dealer_speedLabel = new JLabel("Dealer delay (ms):", JLabel.LEFT);
        dealer_speed = createSlider(100, 10000, dealer_delay);
        control_panel.add(dealer_speedLabel);
        control_panel.add(dealer_speed);

        add(control_panel, BorderLayout.SOUTH);
    }

    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 10);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

        slider.addChangeListener(e -> {
            if (e.getSource() == body_supplier_speed) {
                for (Supplier<Body> supplier : bodySuppliers) {
                    supplier.setDelay(body_supplier_speed.getValue());
                }
            } else if (e.getSource() == motor_supplier_speed) {
                for (Supplier<Motor> supplier : motorSuppliers) {
                    supplier.setDelay(motor_supplier_speed.getValue());
                }
            } else if (e.getSource() == accessory_supplier_speed) {
                for (Supplier<Accessory> supplier : accessorySuppliers) {
                    supplier.setDelay(accessory_supplier_speed.getValue());
                }
            } else if (e.getSource() == dealer_speed) {
                for (Dealer dealer : dealers) {
                    dealer.setDelay(dealer_speed.getValue());
                }
            }
        });

        return slider;
    }

    public void updateStats(int body_storage, int motor_storage, int accessory_storage, int car_storage, int sold_cars) {
        body_storage_label.setText("Body Storage: " + body_storage + "/" + body_storage_capacity);
        motor_storage_label.setText("Motor Storage: " + motor_storage + "/" + motor_storage_capacity);
        accessory_storage_label.setText("Accessory Storage: " + accessory_storage + "/" + accessory_storage_capacity);
        car_storage_label.setText("Car Storage: " + car_storage + "/" + car_storage_capacity);
        sold_cars_label.setText("Sold Cars: " + sold_cars);
    }
}
