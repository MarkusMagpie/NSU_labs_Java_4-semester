package task4.gui;

import task4.factory.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.List;

public class FactoryGUI extends JFrame {
    private final JSlider body_supplier_speed;
    private final JSlider motor_supplier_speed;
    private final JSlider accessory_supplier_speed;
    private final JSlider dealer_speed;

    // labels для оттображения текущих значений ползунков
    private final JLabel body_speed_label;
    private final JLabel motor_speed_label;
    private final JLabel accessory_speed_label;
    private final JLabel dealer_speed_label;

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
        stats_panel.setBorder(new TitledBorder(
                new LineBorder(new Color(70, 130, 180), 4, true), // закруглённые углы
                "Statistics", // Название рамки
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)
        ));

        body_storage_label = new JLabel("Body storage: 0/" + body_storage_capacity, JLabel.LEFT);
        body_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        motor_storage_label = new JLabel("Motor storage: 0/" + motor_storage_capacity, JLabel.LEFT);
        motor_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        accessory_storage_label = new JLabel("Accessory storage: 0/" + accessory_storage_capacity, JLabel.LEFT);
        accessory_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        car_storage_label = new JLabel("Car storage: 0/" + car_storage_capacity, JLabel.LEFT);
        car_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        sold_cars_label = new JLabel("Sold cars: 0", JLabel.LEFT);
        sold_cars_label.setFont(new Font("Arial", Font.BOLD, 14));

        stats_panel.add(body_storage_label);
        stats_panel.add(motor_storage_label);
        stats_panel.add(accessory_storage_label);
        stats_panel.add(car_storage_label);
        stats_panel.add(sold_cars_label);

        add(stats_panel, BorderLayout.CENTER);

        JPanel control_panel = new JPanel();
        control_panel.setLayout(new GridLayout(4, 2));

        // ползунки скоростей + их метки
        body_speed_label = new JLabel("Body supplier delay (ms): " + body_supplier_delay, JLabel.LEFT);
        body_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        body_supplier_speed = createSlider(100, 10000, body_supplier_delay, body_speed_label);
        body_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(body_speed_label);
        control_panel.add(body_supplier_speed);

        motor_speed_label = new JLabel("Motor supplier delay (ms): " + motor_supplier_delay, JLabel.LEFT);
        motor_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        motor_supplier_speed = createSlider(100, 10000, motor_supplier_delay, motor_speed_label);
        motor_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(motor_speed_label);
        control_panel.add(motor_supplier_speed);

        accessory_speed_label = new JLabel("Accessory supplier delay (ms): " + accessory_supplier_delay, JLabel.LEFT);
        accessory_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        accessory_supplier_speed = createSlider(100, 10000, accessory_supplier_delay, accessory_speed_label);
        accessory_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(accessory_speed_label);
        control_panel.add(accessory_supplier_speed);

        dealer_speed_label = new JLabel("Dealer delay (ms): " + dealer_delay, JLabel.LEFT);
        dealer_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        dealer_speed = createSlider(100, 10000, dealer_delay, dealer_speed_label);
        dealer_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(dealer_speed_label);
        control_panel.add(dealer_speed);

        add(control_panel, BorderLayout.SOUTH);
    }

    private JSlider createSlider(int min, int max, int value, JLabel label) {
        JSlider slider = new JSlider(min, max, value);

        slider.setMajorTickSpacing((max - min) / 10);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

//        slider.addChangeListener(this);
        slider.addChangeListener(e -> {
            int slider_value = slider.getValue();
            label.setText(label.getText().split(":")[0] + ": " + slider_value);

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

//    @Override
//    public void stateChanged(ChangeEvent e) {
//        if (e.getSource() == body_supplier_speed) {
//            for (Supplier<Body> supplier : bodySuppliers) {
//                supplier.setDelay(body_supplier_speed.getValue());
//            }
//
//        } else if (e.getSource() == motor_supplier_speed) {
//            for (Supplier<Motor> supplier : motorSuppliers) {
//                supplier.setDelay(motor_supplier_speed.getValue());
//            }
//        } else if (e.getSource() == accessory_supplier_speed) {
//            for (Supplier<Accessory> supplier : accessorySuppliers) {
//                supplier.setDelay(accessory_supplier_speed.getValue());
//            }
//        } else if (e.getSource() == dealer_speed) {
//            for (Dealer dealer : dealers) {
//                dealer.setDelay(dealer_speed.getValue());
//            }
//        }
//    }

    public void updateStats(int body_storage, int motor_storage, int accessory_storage, int car_storage, int sold_cars) {
        body_storage_label.setText("Body storage: " + body_storage + "/" + body_storage_capacity);
        motor_storage_label.setText("Motor storage: " + motor_storage + "/" + motor_storage_capacity);
        accessory_storage_label.setText("Accessory storage: " + accessory_storage + "/" + accessory_storage_capacity);
        car_storage_label.setText("Car storage: " + car_storage + "/" + car_storage_capacity);
        sold_cars_label.setText("Sold cars: " + sold_cars);
    }
}
