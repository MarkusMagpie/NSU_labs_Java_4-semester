# Отчет по 3 заданию "Игра Тетрис с шаблоном проектирования MVC"
В условии нас просят сделать архитектуру программы, основанной на
паттерне `MVC`.
В моем представлении получается что:
 - Модель игры предоставляет данные для отрисовки (игровое поле и текущую фигуру).
 - View занимается именно отрисовкой игрового поля и фигуры по данным.
 - Controller интерпретирует действия пользователя.  

Ниже рассмотрим все классы пекеджа.

## 1 `TetrisModel.java`
```java
public class TetrisModel {
    private int width;
    private int height;
    private TetroMino current_piece;
    private boolean[][] board;
    private int score;
    private boolean paused;

    public TetrisModel(int width, int height) {
        board = new boolean[width][height];
        this.width = width;
        this.height = height;
        SpawnPiece();
        paused = false;
    }

    public void Reset() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                board[x][y] = false;
            }
        }
        score = 0;
        paused = false;
        SpawnPiece();
    }

    private void SpawnPiece() {
        Random random = new Random();
        int shape = random.nextInt(7);
        switch (shape) {
            case 0:
                current_piece = TetroMino.createI();
                break;
            case 1:
                current_piece = TetroMino.createO();
                break;
            case 2:
                current_piece = TetroMino.createT();
                break;
            case 3:
                current_piece = TetroMino.createS();
                break;
            case 4:
                current_piece = TetroMino.createZ();
                break;
            case 5:
                current_piece = TetroMino.createJ();
                break;
            case 6:
                current_piece = TetroMino.createL();
                break;
        }
    }

    public void MovePieceDown() {
        if (paused) return;

        if (CanMove(current_piece, 0, 1)) {
            for (Point p : current_piece.getCoordinates()) {
                p.y += 1;
            }
        } else {
            PlacePiece(); // update coords at board
            SpawnPiece();
            if (!CanMove(current_piece, 0, 0)) {
                JOptionPane.showMessageDialog(null, "GAME OVER", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    public void MovePieceLeft() {
        if (paused) return;

        if (CanMove(current_piece, -1, 0)) {
            for (Point p : current_piece.getCoordinates()) {
                p.x -= 1;
            }
        }
    }

    public void MovePieceRight() {
        if (paused) return;

        if (CanMove(current_piece, 1, 0)) {
            for (Point p : current_piece.getCoordinates()) {
                p.x += 1;
            }
        }
    }

    public void RotatePiece() {
        if (paused) return;

        current_piece.rotate();
        // check if we can't rotate, return to original position
        if (!CanMove(current_piece, 0, 0)) {
            System.out.println("Cannot rotate object");
            current_piece.rotate();
            current_piece.rotate();
            current_piece.rotate();
        }
    }

    private void PlacePiece() {
        for (Point p : current_piece.getCoordinates()) {
            if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
                board[p.x][p.y] = true;
            }
        }
        ClearRows();
    }

    private void ClearRows() {
        for (int y = height - 1; y >= 0; --y) {
            boolean full_row = true;
            for (int x = 0; x < width; ++x) {
                if (!board[x][y]) {
                    full_row = false;
                    break;
                }
            }

            if (full_row) {
                score += 100;
                for (int row = y; row > 0; --row) {
                    for (int col = 0; col < width; ++col) {
                        board[col][row] = board[col][row - 1];
                    }
                }
                for (int col = 0; col < width; ++col) {
                    board[col][0] = false;
                }
                ++y;
            }
        }
    }

    private boolean CanMove(TetroMino piece, int x, int y) {
        for (Point p : piece.getCoordinates()) {
            int new_x = p.x + x;
            int new_y = p.y + y;
            if (new_x < 0 || new_x >= width || new_y >= height || new_y < 0) {
                return false;
            }
            if (board[new_x][new_y]) {
                return false;
            }
        }
        return true;
    }

    public int GetScore() {
        return score;
    }

    public TetroMino GetCurrentPiece() {
        return current_piece;
    }

    public boolean[][] GetBoard() {
        return board;
    }

    public boolean IsGameOver() {
        return false;
    }

    public void SetPause(boolean pause) {
        paused = pause;
    }

    public boolean GetPause() {
        return paused;
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }
}
```
`TetrisModel` - модель данных для игры "Тетрис". 
Класс отвечает за управление состоянием игрового поля, 
текущей фигуры и игровой логики. Он включает в себя следующие основные функции:

 - Управление текущей фигурой (`TetroMino` - фигура в игре тетрис так назыается).
 - Обновление игрового поля.
 - Проверка возможности движения фигур.
 - Подсчёт очков и определение игровых событий (завершение игры либо пауза).

Сначала рассмотрим поля класса: 
1. `private int width, height` - размеры игрового поля
2. `private TetroMino current_piece;` - текущая фигура на игровом поле
3. `private boolean[][] board;` - двумерный массив булевых значений, 
представляющий состояние клеток игрового поля (занята или нет)
4. `private int score;` - текущий счет игрока
5. `private boolean paused;` - флаг, на паузе игра или нет.

Теперь рассмотрим методы класса:
### 1.1 Конструктор `public TetrisModel(int width, int height)`
```java
public TetrisModel(int width, int height) {
    board = new boolean[width][height];
    this.width = width;
    this.height = height;
    SpawnPiece();
    paused = false;
}
```
Просто задаем стартовые значения всем полям класса. То есть инициализируем 
игровое поле заданной ширины и высоты. Создаётся пустая доска и вызывается 
метод `SpawnPiece()` для появления первой фигуры.

### 1.2 Сброс игры `public void Reset()`
```java
public void Reset() {
    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            board[x][y] = false;
        }
    }
    score = 0;
    paused = false;
    SpawnPiece();
}
```
Очищаем двумерный булеан массив, то есть игровое поле, затем сбрасываем счет
и снимаем игру с паузы. Ну и генерируем новую фигуру на игрововм поле.

### 1.3 Генерация новой фигуры `private void SpawnPiece()`
```java
private void SpawnPiece() {
        Random random = new Random();
        int shape = random.nextInt(7);
        switch (shape) {
            case 0:
                current_piece = TetroMino.createI();
                break;
            case 1:
                current_piece = TetroMino.createO();
                break;
            case 2:
                current_piece = TetroMino.createT();
                break;
            case 3:
                current_piece = TetroMino.createS();
                break;
            case 4:
                current_piece = TetroMino.createZ();
                break;
            case 5:
                current_piece = TetroMino.createJ();
                break;
            case 6:
                current_piece = TetroMino.createL();
                break;
        }
    }
```
Здесь вспоминаем что есть поле класса `private TetroMino current_piece`, как раз
отвечающее за то какая фигура сейчас на поле. Ну и этот метод как раз ее и задает.  
Я случайным образом с помощью класса `Random` выбираю число от 0 до 6 и задаю
соответствующую фигуру.  
Тетромины смотрел [здесь](https://tetris.wiki/Tetromino#:~:text=The%20seven%20one-sided%20tetrominoes,previously%20called%20tetraminoes%20around%201999.).

### 1.4 Методы для движения фигуры вниз, влево и вправо
 - **1.4.1** Движение вниз  
```java
public void MovePieceDown() {
        if (paused) return;

        if (CanMove(current_piece, 0, 1)) {
            for (Point p : current_piece.getCoordinates()) {
                p.y += 1;
            }
        } else {
            PlacePiece();
            SpawnPiece();
            if (!CanMove(current_piece, 0, 0)) {
                JOptionPane.showMessageDialog(null, "GAME OVER", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
```
Если движение вниз возможно, что проверяется с помощью метода `CanMove()` о котором
я расскажу позже, то все координаты фигуры по игреку инкриминируем.  
Если же движение вниз невозможно, то закрепляю фигуру внизу поля методом `PlacePiece()`.  

Что важно, метод `PlacePiece()` вызывает метод `ClearRows()` который 
проверяет заполнен ли какой-то начиная с нижнего ряда ряд фигурами и если да, то удаляет его
и добавляет игроку 100 очков.  

После закрепления фигуры я спавню снизу новую 
методом `SpawnPiece()` и если она не может никуда двигаться то все. Конец игры. 
 - **1.4.2** Движение влево
```java
public void MovePieceLeft() {
    if (paused) return;

    if (CanMove(current_piece, -1, 0)) {
        for (Point p : current_piece.getCoordinates()) {
            p.x -= 1;
        }
    }
}
```
Если на паузе, то ничего; проверяем возможность движения влево и если можно, то
все координаты по иксу двигаем влево.
 - **1.4.3** Движение вправо аналогично движению влево
  
### 1.5 Поворот фигуры `RotatePiece()`
```java
public void RotatePiece() {
    if (paused) return;

    current_piece.rotate();
    if (!CanMove(current_piece, 0, 0)) {
        System.out.println("Cannot rotate object");
        current_piece.rotate();
        current_piece.rotate();
        current_piece.rotate();
    }
}
```
Поворачиваем фигуру. Если поворот недопустим (выходим за границы или пересекаемся
с другими блоками), то фигура возвращается в исходное положение.
Опять обращаю внимание, что метод `rotate()` принадлежит классу `TetroMino.java`. 
Рассмотрим его позже. 

### 1.6 Вспомогательные методы для движения фигуры
Эти методы применялись выше и сейчас просто вкратце как они работают.
```java
private void PlacePiece() {
    for (Point p : current_piece.getCoordinates()) {
        if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
            board[p.x][p.y] = true;
        }
    }
    ClearRows();
}
```
Метод `PlacePiece()` проверяет что каждая пара координат лежит в пределах поля
и если это так, то помечает эти координаты на игровом поле как заполненные.  
Далее если вдруг у нас заполнилась строка она очистится методом `ClearRows()` и 
при этом нам(игроку) добавят 100 очков.

```java
private void ClearRows() {
    for (int y = height - 1; y >= 0; --y) {
        boolean full_row = true;
        for (int x = 0; x < width; ++x) {
            if (!board[x][y]) {
                full_row = false;
                break;
            }
        }

        if (full_row) {
            score += 100;
            for (int row = y; row > 0; --row) {
                for (int col = 0; col < width; ++col) {
                    board[col][row] = board[col][row - 1];
                }
            }
            for (int col = 0; col < width; ++col) {
                board[col][0] = false;
            }
            ++y;
        }
    }
}
```
Проходим по строкам игрового поля снизу вверх. Если вдруг строка полностью заполнена,
то она удаляется (заполняется нулями) и строки которые выше помещаются вниз.
Ну и +100 очков игроку.
```java
private boolean CanMove(TetroMino piece, int x, int y) {
    for (Point p : piece.getCoordinates()) {
        int new_x = p.x + x;
        int new_y = p.y + y;
        if (new_x < 0 || new_x >= width || new_y >= height || new_y < 0) {
            return false;
        }
        if (board[new_x][new_y]) {
            return false;
        }
    }
    return true;
}
```
В аргументах нам даны координаты на которые мы хотим сместиться. Мы их задаем
переменными `new_x` и `new_y` и проверяем вылезаем ли мы за границы. Также нужно
проверять занятость клеток так как на поле уже могут лежать фигуры. 
  
## 2 `TetroMino.java`
```java
public class TetroMino {
    private final Point[] coordinates;
    private final Color color;
    
    public TetroMino(Point[] coordinates, Color color) {
        this.coordinates = coordinates;
        this.color = color;
    }

    public void rotate() {
        Point center = coordinates[1];
        for (Point coordinate : coordinates) {
            int x = coordinate.x - center.x;
            int y = coordinate.y - center.y;
            coordinate.x = center.x - y;
            coordinate.y = center.y + x;
        }
    }

    public static TetroMino createI() {
        return new TetroMino(new Point[]{
                new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)
        }, Color.CYAN);
    }

    public static TetroMino createO() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(2, 1)
        }, Color.YELLOW);
    }

    public static TetroMino createT() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.MAGENTA);
    }

    public static TetroMino createS() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)
        }, Color.GREEN);
    }

    public static TetroMino createZ() {
        return new TetroMino(new Point[]{
                new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)
        }, Color.RED);
    }

    public static TetroMino createJ() {
        return new TetroMino(new Point[]{
                new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.BLUE);
    }

    public static TetroMino createL() {
        return new TetroMino(new Point[]{
                new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.ORANGE);
    }

    public Point[] getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }
}
```
Класс `TetroMino` отвечает за описание тетромино - фигур которыми мы управляем. 
Каждая фигура состоит из четырех блоков, которые описываются 
координатами `Point[]` относительно игрового поля и имеют свой цвет `Color`.
На всякий случай поля класса `Point` это два `int`: `x` и `y`. 
[Документация класса Point](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)  
[Документация класса Color](https://docs.oracle.com/javase/8/docs/api/java/awt/Color.html)  

### 2.1 Конструктор
```java
public TetroMino(Point[] coordinates, Color color) {
    this.coordinates = coordinates;
    this.color = color;
}
```
Тут единственное, что можно оговорить это использование ключевого слова [this](https://www.w3schools.com/java/ref_keyword_this.asp):   
> `this` keyword is to eliminate the confusion between class  
> attributes and parameters with the same name 
> (because a class attribute is shadowed by a method or constructor parameter).

### 2.2 Ротация тетромин
```java
public void rotate() {
    Point center = coordinates[1];
    for (Point coordinate : coordinates) {
        int x = coordinate.x - center.x;
        int y = coordinate.y - center.y;
        coordinate.x = center.x - y;
        coordinate.y = center.y + x;
    }
}
```
Здесь выполняем поворот фигуры на 90 градусов по часовой стрелке.
Задействуем матрицу поворота для изменения координат.  
Центр поворота задается вторым блоком любой фигуры `coordinates[1]`.  
Для каждого блока применяем следующую логику:
 * вычисляется смещение блока относительно центра
 * применяется поворот с обновлением координат.  
![Матрица вращения](https://www.google.com/search?client=opera&hs=Krw&sca_esv=43b759f74936773f&q=матрица+поворота+координат+в+двумерном+пространстве&udm=2&fbs=ABzOT_DY0L9H0KCwqgk1S0A8cLGnJkIotKLGfgRWBDtt9w6W0Qlp3Mr-FQ0hKsAcPS0cQ6tMUS1cOMAJ4Aec6ZqRobMv13A_IdXhh0ZiwzJth_fcR04QpJYtyFCTrtSsKcVj7vEs7fkYTcXh_GsYToKY1-OERVjxkUhmTUYzdiLteJiPca29UTgRjw7TQE63UJp4d4L1CHeSMgykbkR9JLXuWrk2Svlwvw&sa=X&ved=2ahUKEwiVwYfd2I2LAxW_hP0HHbM8DVMQtKgLegQIDhAB&biw=1434&bih=880&dpr=1#vhid=QcNh2YK6FrS1FM&vssid=mosaic)
  
### 2.3 Методы создания тетромино
Разберу на 1 примере так как они однотипные. В качестве примера выбран метод `createI`:
```java
public static TetroMino createI() {
    return new TetroMino(new Point[]{
            new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)
    }, Color.CYAN);
}
```
Вызывается конструктор который я выше уже разбирал и ему параметрами передаю:
* Координаты: горизонтальная линия из 4 блоков.
* Цвет выбран произвольно - циановый.
  
## 3 `HighScores.java`
```java
public class HighScores {
    private List<ScoreEntry> scores;
    private final String filename = "task3/src/main/resources/highscores.txt";

    public HighScores() {
        scores = new ArrayList<>();
        LoadScores();
    }

    public void LoadScores() {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int time = Integer.parseInt(parts[2]);
                    scores.add(new ScoreEntry(name, score, time));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOE exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void SaveScores() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            for (ScoreEntry entry : scores) {
                out.write(entry.GetName() + "," + entry.GetScore() + "," + entry.GetTime() + "\n");
            }
        } catch (IOException e) {
            System.out.println("IOE exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void AddScore(String name, int score, int time) {
        scores.add(new ScoreEntry(name, score, time));
        if (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }
        SaveScores();
    }

    public void ShowHighScores() {
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry entry : scores) {
            sb.append(entry.GetName()).append(" - ").append(entry.GetScore()).append(" - ").append(entry.GetTime()).append("s\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
    
    public List<Integer> GetScores() {
        List<Integer> scores = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            scores.add(entry.GetScore());
        }
        return scores;
    }

    public List<String> GetNames() {
        List<String> names = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            names.add(entry.GetName());
        }
        return names;
    }

    public List<Integer> GetTimes() {
        List<Integer> times = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            times.add(entry.GetTime());
        }
        return times;
    }

    private class ScoreEntry {
        private String name;
        private int score;
        private int time;

        ScoreEntry(String name, int score, int time) {
            this.name = name;
            this.score = score;
            this.time = time;
        }

        public String GetName() {
            return name;
        }

        public int GetScore() {
            return score;
        }

        public int GetTime() {
            return time;
        }
    }
}
```
`HighScores` отвечает за управление списком рекордов в виде списка объектов 
внутреннего класса.  
Сначала рассмотрим поля класса:
1.  `private List<ScoreEntry> scores` - здесь каждый элемент - объект 
внутреннего класса `ScoreEntry`, который содержит имя игрока, 
количество очков которое он набрал и время игры в секундах.
2.  `private final String filename = "task3/src/main/resources/highscores.txt"` - это 
путь к файлу используемого для хранения и чтения объектов класса `ScoreEntry`.

### 3.1 Конструктор `HighScores()`
```java
public HighScores() {
    scores = new ArrayList<>();
    LoadScores();
}
```
Создаем пустой класс `ArrayList`, для заполнения в процессе игры и 
вызываем метод `LoadScores()`.
### 3.2 Метод `LoadScores()`
```java
public void LoadScores() {
    try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                int time = Integer.parseInt(parts[2]);
                scores.add(new ScoreEntry(name, score, time));
            }
        }
    } catch (FileNotFoundException e) {
        System.out.println("File not found: " + e.getMessage());
    } catch (IOException e) {
        System.out.println("IOE exception: " + e.getMessage());
        e.printStackTrace();
    }
}
```
С помощью метода [BufferedReader.readLine()](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#readLine--) 
мы получаем строку, затем методом `split()` мы делим эту строку на подстроки
разделенные сепаратором (запятой). 
Полученные данные добавляем методом `add()` добавляем в список `scores`.

### 3.3 Метод `SaveScores()`
```java
public void SaveScores() {
    try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
        for (ScoreEntry entry : scores) {
            out.write(entry.GetName() + "," + entry.GetScore() + "," + entry.GetTime() + "\n");
        }
    } catch (IOException e) {
        System.out.println("IOE exception: " + e.getMessage());
        e.printStackTrace();
    }
}
```
Сохраняем текущий список `scores` в текстовый файл. Для этого проходим 
все элементы списка и через запятую пишем в `highscores.txt`.  
Замечу что этот метод вызывается в двух местах:
1. если игрок начинает игру заново (нажал "New Game").
2. если он нажал "Exit".

### 3.4 Метод `AddScore(String name, int score, int time)`
```java
public void AddScore(String name, int score, int time) {
    scores.add(new ScoreEntry(name, score, time));
    if (scores.size() > 10) {
        scores.remove(scores.size() - 1);
    }
    SaveScores();
}
```
Это метод для добавления новой записи в список и сохранения её в файл 
методом `SaveScores()`.  
Перед сохранением записи в файл проверяем что сейчас в файле не более 10
игроков. Если их все-таки больше, то удаляем последний.

### 3.5 Метод `ShowHighScores()`
```java
public void ShowHighScores() {
    StringBuilder sb = new StringBuilder();
    for (ScoreEntry entry : scores) {
        sb.append(entry.GetName()).append(" - ").append(entry.GetScore()).append(" - ").append(entry.GetTime()).append("s\n");
    }
    JOptionPane.showMessageDialog(null, sb.toString());
}
```
Это буквально реализация команды "High Scores" из условия. 
Здесь мы выводим диалоговое окно (`JOptionPane` из библиотеки `Java Swing`) с
информацией о рекордах различных пользователей.  
Примеры [showMessageDialog](https://www.javatpoint.com/java-joptionpane).

### 3.6 Внутренний класс `ScoreEntry`
```java
private class ScoreEntry {
    private String name;
    private int score;
    private int time;

    ScoreEntry(String name, int score, int time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public String GetName() {
        return name;
    }

    public int GetScore() {
        return score;
    }

    public int GetTime() {
        return time;
    }
}
```
Объекты изначально созданного листа `scores` как раз являются объектами
этого класса. Здесь конструктор и геттеры полей (инкапсуляция).

## 4 `TetrisView.java`
```java
public class TetrisView extends JPanel {
    private TetrisModel model;
    private final int cell_size = 30;

    public TetrisView(TetrisModel model) {
        this.model = model;
        setPreferredSize(new Dimension(cell_size * model.GetWidth(), cell_size * model.GetHeight()));
        setBackground(new Color(83, 83, 83));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawBoard(g, model.GetBoard());
        DrawCurrentPiece(g, model.GetCurrentPiece());
    }
    
    private void DrawBoard(Graphics g, boolean[][] board) {
        for (int y = 0; y < board[0].length; y++) {
            for (int x = 0; x < board.length; x++) {
                if (board[x][y]) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x * cell_size, y * cell_size, cell_size, cell_size);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
                }
            }
        }
    }

    private void DrawCurrentPiece(Graphics g, TetroMino piece) {
        g.setColor(piece.getColor());
        for (Point p : piece.getCoordinates()) {
            g.fillRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
            g.setColor(Color.BLACK);
            g.drawRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
        }
    }
    
    public int GetCellSize() {
        return cell_size;
    }
}
```
Наследование от `JPanel` позволяет создавать пользовательскую 
графическую панель.

Поля в данном классе - размер одной клетки `cell_size` и 
уже созданная модель `model`.

### 4.1 Конструктор `TetrisView(TetrisModel model)`
```java
public TetrisView(TetrisModel model) {
    this.model = model;
    setPreferredSize(new Dimension(cell_size * model.GetWidth(), cell_size * model.GetHeight()));
    setBackground(new Color(83, 83, 83));
}
```
Устанавливает размер панели `JPanel` в зависимости от размеров поля модели 
(`width`, `height`) и размера ячейки `cell_size`. Панель серого цвета.

### 4.2 Перегруженный метод `paintComponent(Graphics g)`
```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    DrawBoard(g, model.GetBoard());
    DrawCurrentPiece(g, model.GetCurrentPiece());
}
```
Этот метод перерисовывает графическое представление панели.  
`super.paintComponent(g)` очищает предыдущие отрисовки.  
Также вызываются методы:
 * `DrawBoard(g, model.GetBoard())` - отрисовка самого поля.
 * `DrawCurrentPiece(g, model.GetCurrentPiece())` - отображение текущей
падающей фигуры.

### 4.3 Метод `DrawBoard(Graphics g, boolean[][] board)`
```java
private void DrawBoard(Graphics g, boolean[][] board) {
    for (int y = 0; y < board[0].length; y++) {
        for (int x = 0; x < board.length; x++) {
            if (board[x][y]) {
                g.setColor(Color.GRAY);
                g.fillRect(x * cell_size, y * cell_size, cell_size, cell_size);
                g.setColor(Color.BLACK);
                g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
            } else {
                g.setColor(Color.BLACK);
                g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
            }
        }
    }
}
```
Отрисовываем игровое поле на основе данных из двумерного массива `boolean[][] board`.  
Мы перебираем каждую ячейку игрового поля и если она занята 
то есть уже уложен какой-то тетромин, то
заполняем серым цветом прямоугольник (метод [fillRect](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics.html#fillRect-int-int-int-int-)),
а черным обводим вокруг (метод [drawRect](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics.html#drawRect-int-int-int-int-)) 
типа как граница ячейки.  
Ну и если ячейка пуста то очевидно красим только черную границу.

### 4.4 Метод `DrawCurrentPiece(Graphics g, TetroMino piece)`
```java
private void DrawCurrentPiece(Graphics g, TetroMino piece) {
    g.setColor(piece.getColor());
    for (Point p : piece.getCoordinates()) {
        g.fillRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
        g.setColor(Color.BLACK);
        g.drawRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
    }
}
```
Отрисовываем текущую падающую фигуру-тетромину:
для каждой координаты фигуры заполняем ее цветом соответствующей тетромины
 и черным обводим вокруг прямоугольник как границу.

## 5 `TimerPanel.java`
```java
public class TimerPanel extends JPanel {
    private JLabel timer_label;
    private Timer timer;
    private int elapsed_time; 

    public TimerPanel() {
        timer_label = new JLabel("Time: 0");
        setLayout(new BorderLayout());

        add(timer_label, BorderLayout.CENTER);

        elapsed_time = 0;
        
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed_time++;
                timer_label.setText("Time: " + elapsed_time);
            }
        });
    }

    public void StartTimer() {
        timer.start();
    }

    public void StopTimer() {
        timer.stop();
    }

    public void ResetTimer() {
        elapsed_time = 0;
        timer_label.setText("Time: 0");
    }

    public int GetElapsedTime() {
        return elapsed_time;
    }
}
```
### 5.1 Конструктор `TimerPanel()`
```java
public TimerPanel() {
    timer_label = new JLabel("Time: 0");
    setLayout(new BorderLayout());

    add(timer_label, BorderLayout.CENTER);

    elapsed_time = 0;
    
    timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            elapsed_time++;
            timer_label.setText("Time: " + elapsed_time);
        }
    });
}
```
В конструкторе создаем метку для отображения времени JLabel(), 
используем `BorderLayout()` для расположения метки по центру.  
> [!NOTE]
> [How To Use BorderLayout](https://docs.oracle.com/javase/tutorial/uiswing/layout/border.html) 
> здесь можно прочесть о добавлении элементов в конкретное место JFrame.

Далее создаем переменную следящую за временем игры `elapsed_time`.  
Теперь самое сложное: создание таймера:
```java
timer = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        elapsed_time++;
        timer_label.setText("Time: " + elapsed_time);
    }
});
```
 - таймер срабатывает каждый 1000 мс - 1 сек.
 - при каждом срабатывании таймера срабатывает метод `actionEvent`:
> `ActionListener` — это интерфейс, предоставляемый библиотекой `Swing`. 
> Он используется для обработки событий - в нашем случае - срабатывания 
> таймера `Timer`, что происходит раз в секунду.  
> 
> Метод `actionPerformed` вызывается каждый раз когда возникает событие,
> связанное с таймером.

Все остальные методы класса мелочные и однострочные, нет смысла разбирать.

## 6 `ScorePanel.java`
```java
public class ScorePanel extends JPanel {
    private JLabel score_label;
    private TetrisModel model;

    public ScorePanel(TetrisModel model) {
        this.model = model;
        score_label = new JLabel("Score: 0");
        setLayout(new BorderLayout());
        add(score_label, BorderLayout.CENTER);
    }

    public void UpdateScore() {
        score_label.setText("Score: " + model.GetScore());
    }
}
```
`ScorePanel` это панель для отображения текущего счёта игрока. 
Замечу что сам класс расщиряет класс `JPanel`, поэтому когда мы
используем метод `add` мы будем **добавлять метку на панель**.  
Поля класса:
1. `private JLabel score_label` - метка для отображения текста.
2. `private TetrisModel model` - модель игры, откуда мы берем 
текущее значение очков для обновления в методе `UpdateScore()`.
  
### 6.1 Конструктор `ScorePanel`
Инициаизируем метку `score_label` с нулевым счетом и помещаем ее в центр 
панели.

### 6.2 Метод `UpdateScore()`
Тупо меняем текст `score_label` на текущее значение очков игрока с помощью
`model.GetScore()`.

## 7 `TetrisController.java`
```java
public class TetrisController implements KeyListener {
    private TetrisModel model;
    private TetrisView view;
    private HighScores hs;

    private TimerPanel timer_panel;
    private Timer game_timer;

    private String player_name;

    public TetrisController(TetrisModel model, TetrisView view, HighScores hs, TimerPanel timer_panel) {
        this.model = model;
        this.view = view;
        this.hs = new HighScores();
        this.timer_panel = timer_panel;
        timer_panel.StartTimer();

        player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);
        
        game_timer = new Timer(1000, e -> {
            if (!model.GetPause()) {
                model.MovePieceDown();
                view.repaint();
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (model.GetPause()) return;
     
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (model.GetPause()) {
                ResumeGame();
            } else {
                PauseGame();
            }
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                model.MovePieceLeft();
                break;
      
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                model.MovePieceRight();
                break;
      
            case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                model.MovePieceDown();
                break;
      
            case KeyEvent.VK_UP, KeyEvent.VK_W:
                model.RotatePiece();
                break;
      
            case KeyEvent.VK_Q:
                ExitVerification();
                break;
        }
        view.repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void PauseGame() {
        model.SetPause(true);
        timer_panel.StopTimer();
    }

    public void ResumeGame() {
        model.SetPause(false);
        timer_panel.StartTimer();
    }

    public void StartNewGame() {
        PauseGame();
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to start new game?", "New Game Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("New game started");

            if (player_name != null) {
                hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
            }
            player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);

            model.Reset();
            view.repaint();
            timer_panel.ResetTimer();
            timer_panel.StartTimer();
        } else {
            ResumeGame();
        }
    }

    public void ShowHighScores() {
        PauseGame();
        hs.ShowHighScores();
        System.out.println("Showed high scores");
        ResumeGame();
    }

    public void ShowAbout() {
        PauseGame();
        String about = "NSU 4th semester\nTetris\nMathew Sorokin";
        JOptionPane.showMessageDialog(null, about, "About", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Showed information about game");
        ResumeGame();
    }

    public void ExitVerification() {
        PauseGame();
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
            System.out.println("Added new score: " + player_name + " - " + model.GetScore() + " - " + timer_panel.GetElapsedTime() + "s");
            System.out.println("Вы вышли из игры");
            System.exit(0);
        }
        ResumeGame();
    }
}
```
Обращаю внимание что класс имплементирует интерфейс `KeyListener`, а
значит может использовать все его методы, нас интересует `keyPressed()`. 
Примеры [здесь](https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyListener.html#keyPressed-java.awt.event.KeyEvent-).  

Класс `TetrisController` связывает пользовательский ввод 
с соответствующими действиями в модели `TetrisModel` и 
обновлением представления `TetrisView`. 

Он также управляет таймером игры, обработкой паузы, 
запуском новой игры, отображением информации и выходом из игры - это 
команды которые доступны игроку во время игры.

### 7.1 Конструктор `TetrisController(...)`
```java
public TetrisController(TetrisModel model, TetrisView view, HighScores hs, TimerPanel timer_panel) {
    this.model = model;
    this.view = view;
    this.hs = new HighScores();
    this.timer_panel = timer_panel;
    timer_panel.StartTimer();

    player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);
    
    game_timer = new Timer(1000, e -> {
        if (!model.GetPause()) {
            model.MovePieceDown();
            view.repaint();
        }
    });
}
```
Получает ссылки на модель, представление, панель таймера и таблицу рекордов.  
С помощью метода [JOptionPane.showInputDialog()](https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html#showInputDialog-java.awt.Component-java.lang.Object-java.lang.String-int-) показывает 
диалоговое окно для ввода имени игрока при старте игры.

Настройка `game_timer`:  
Каждую секунду если игра не на паузе, 
то вызываем `MovePieceDown()` и обновляем представление методом `repaint()`.
> Пример использования `repaint()` смотри [здесь](https://www.javatpoint.com/repaint-method-in-java)
  
### 7.2 Перегрузка метода `keyPressed(KeyEvent e)`
```java
@Override
public void keyPressed(KeyEvent e) {
    if (model.GetPause()) return;
 
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        if (model.GetPause()) {
            ResumeGame();
        } else {
            PauseGame();
        }
    }

    switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT, KeyEvent.VK_A:
            model.MovePieceLeft();
            break;
  
        case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
            model.MovePieceRight();
            break;
  
        case KeyEvent.VK_DOWN, KeyEvent.VK_S:
            model.MovePieceDown();
            break;
  
        case KeyEvent.VK_UP, KeyEvent.VK_W:
            model.RotatePiece();
            break;
  
        case KeyEvent.VK_Q:
            ExitVerification();
            break;
    }
    view.repaint();
}
```
Все понятно, скажу только что после ЛЮБОГО действия вызывается 
`view.repaint()`.

### 7.3 Пауза и продолжение игры 
```java
public void PauseGame() {
    model.SetPause(true);
    timer_panel.StopTimer();
}

public void ResumeGame() {
    model.SetPause(false);
    timer_panel.StartTimer();
}
```
Метод `PauseGame()` используется каждый раз когда игрок выбирает какое-то
действие (NewGame, Scores и т.д.), а `ResumeGame()` будет вызываться
при выходе из окон выбранных комманд.
> [!NOTE] мб добавить надпись куда-нибудь что типа "Game Paused" 
> но пока все примитивно вряд ли стоит.

### 7.4 Первый метод доступный игроку `StartNewGame`
```java
public void StartNewGame() {
    PauseGame();
    int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to start new game?", "New Game Confirmation", JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION) {
        System.out.println("New game started");

        if (player_name != null) {
            hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
        }
        player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);

        model.Reset();
        view.repaint();
        timer_panel.ResetTimer();
        timer_panel.StartTimer();
    } else {
        ResumeGame();
    }
}
```
Ну при нажатии на эту команду игра ставится на паузу и вылазит `JOptionPane`
с вопросом действительно ли начать заново?
 - Если "да", то если рекорд игрока еще не в файле `highscores.txt` то
он туда занесется, затем игрока опять спрашивают его имя, перерисовываем 
поле, обновляем таймер игры и запускаем его.
> Напоминаю что конструктор TimerPanel() уже срабатывал и таймер не изменился:
> ```java
> timer = new Timer(1000, new ActionListener() {
>     @Override
>     public void actionPerformed(ActionEvent e) {
>         elapsed_time++;
>         timer_label.setText("Time: " + elapsed_time);
>     }
> });
> ```
 - Если "нет", то просто снимаем паузу и продолжаем.

Остальные методы работают по схожей логике:
ставим игру на паузу и показываем игроку то, что он выбрал, затем продолжаем
игру сняв паузу.

## 8 `TetrisMenuBar.java`
```java
public class TetrisMenuBar extends JMenuBar {
    public TetrisMenuBar(TetrisController controller) {
        JMenu game_menu = new JMenu("Game");
        JMenuItem new_game_item = new JMenuItem("New Game");
        JMenuItem exit_item = new JMenuItem("Exit");
        JMenuItem high_scores_item = new JMenuItem("High Scores");
        JMenuItem about = new JMenuItem("About");

        new_game_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.StartNewGame();
            }
        });

        high_scores_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ShowHighScores();
            }
        });

        exit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ExitVerification();
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ShowAbout();
            }
        });

        game_menu.add(new_game_item);
        game_menu.add(high_scores_item);
        game_menu.add(about);
        game_menu.add(exit_item);

        add(game_menu);
    }
}
```
Этот класс напрямую связан с классом `TetrisController`.
Создаем объект класса `JMenu` и добавляем к нему 4 объекта `JMenuItem`.
Это и есть объекты которые может выбрать игрок.  
Для каждого объекта `JMenuItem` создается обработчик событий
`ActionListener`, который вызывает соответствующий метод
из `TetrisController` (пункт 7 выше).

Опять обращаю внимание что `class TetrisMenuBar extends JMenuBar` а значит, 
что объект `JMenu` мы в самой последней строке добавляем в `JMenuBar`:
```java
add(game_menu);
```
> Хороший пример работы с классами `JMenuBar, JMenu` можно посмотреть [здесь](https://www.javatpoint.com/java-jmenuitem-and-jmenu).
  
Также для добавления обработчика событий можно использовать лямбда-выражения.
То есть вместо: 
```java
new_game_item.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.StartNewGame();
    }
});
```
Можно писать так:
```java
new_game_item.addActionListener(e -> controller.StartNewGame());
```
И выполняется абсолютно 1 в 1.

## 9 Главный класс `Main.java`
```java
public class Main {
    public static void main(String[] args) {
        TetrisModel model = new TetrisModel(10, 20); // Model
        TetrisView view = new TetrisView(model); // View
        HighScores hs = new HighScores();
        TimerPanel timer_panel = new TimerPanel();
        ScorePanel score_panel = new ScorePanel(model);
        TetrisController controller = new TetrisController(model, view, hs, timer_panel); // Controller

        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(view.GetCellSize() * model.GetWidth() + 12, view.GetCellSize() * model.GetHeight() + 90);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());
        frame.add(view, BorderLayout.CENTER);
        frame.add(score_panel, BorderLayout.SOUTH);
        frame.add(timer_panel, BorderLayout.NORTH);
        frame.addKeyListener(controller);

        TetrisMenuBar menu_bar = new TetrisMenuBar(controller);
        frame.setJMenuBar(menu_bar);

        frame.setVisible(true);

        while (!model.IsGameOver()) {
            try {
                Thread.sleep(500);
                model.MovePieceDown();
                view.repaint();
                score_panel.UpdateScore();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("GAME OVER!");
    }
}
```
