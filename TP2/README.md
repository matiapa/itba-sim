## Usage

1. Install [FFMPEG](https://ffmpeg.org/download.html)
2. Copy .env.template to .env and point 'ffmpeg_path' to ffmpeg.exe path
3. **Build the project**: ``mvn package``
4. Copy config.json.template to config.json and choose your config
5. **Run the project**: ``java -jar target/run.jar config.json``

## 2D Patterns

### Rule 1112

#### Expanding Oscillator

``(L=100, T=50) [[50,50,50]]``

### Rule 3623

#### Replicator

``(L=100, T=100, fps=6) [[48,50],[48,51],[48,52],[49,49],[49,52],[50,48],
[50,52],[51,48],[51,51],[52,48],[52,49],[52,50]]``

### Rule 3323

#### Glider gun

``(L=100, T=100, fps=12) [[0,24],[1,22],[1,24],[2,12],[2,13],[2,20],[2,21],[2,34],[2,35],[3,11],[3,15],[3,20],
[3,21],[3,34],[3,35],[4,0],[4,1],[4,10],[4,16],[4,20],[4,21],[5,0],[5,1],[5,10],
[5,14],[5,16],[5,17],[5,22],[5,24],[6,10],[6,16],[6,24],[7,11],[7,15],[8,12],[8,13]``

## 3D Patterns

### Rule 5556

#### Oscillator

``(L=5, T=5) [[2,1,1],[2,1,2],[2,1,3],[2,2,2],[2,3,1],[2,3,2],[2,3,3]]``

### Rule 6657

#### Glider
``(L=15, T=20) [[1,1,1],[1,2,1],[1,2,3],[1,3,1],[1,3,2],
[2,1,1],[2,2,1],[2,2,3],[2,3,1],[2,3,2]]``

### Rule 2645

#### Expansion
``(L=25, T=10) [12,14,10],[13,14,10],[12,12,12],[12,11,11],[12,14,15],[12,13,15]``