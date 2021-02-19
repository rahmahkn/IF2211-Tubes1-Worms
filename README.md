# IF2211-Tubes1-Worms

Program ini merupakan bot yang digunakan untuk menyusun strategi greedy yang bertujuan untuk memenangkan game Worms.

## Algoritma Greedy yang Diimplementasikan
Element algoritma greedy:
1.Himpunan kandidat: Semua cell pada map
  Semua cell yang berada di map merupakan kandidat dari algoritma greedy.
  
2.Himpunan solusi: Cell yang memiliki radius 1 dari current worm
  Cell yang berada di depan, belakang, kiri dan kanan cacing.
  
3.Fungsi solusi: Fungsi memeriksa apakah cell yang dipilih dapat mengantarkan bot kami menuju ke destinasi dan menambah point tim kami
  Memastikan bahwa cell yang dipilih tidak membuat worm menjauh dari destinasi dan command yang dipilih dapat menambah point tim kami.
  
4.Fungsi seleksi: Memeriksa apakah cell yang terpilih merupakan cell yang aman dan dapat memilih command yang paling optimal
  Memastikan bahwa cell yang dipilih bukan bertipe lava dan belum ditempati oleh worm lain serta memilih command yang paling optimal untuk round tersebut.
  
5.Fungsi kelayakan: Fungsi memeriksa apakah cell berada di koordinat yang valid
  Koordinat yang valid merupakan koordinat yang bisa ditempati.
  
6.Fungsi objektif: Jarak yang ditempuh oleh worm menuju destinasi merupakan jarak minimum dan memaksimalkan score.
  Tujuan dari implementasi algoritma greedy adalah agar worm dapat sampai di tempat tujuan dengan menempuh jarak yang minimum dan score akhir tim kami dapat maksimal.

   Algoritma greedy yang paling mangkus saat digunakan untuk memenangkan permainan adalah algoritma greedy by cells. Algoritma greedy by cells dikembangkan dari algoritma greedy by attack dan menggabungkannya dengan pemilihan cell yang optimal sehingga ketika worm kami sedang tidak menemukan lawan di area sekitarnya, worm akan memilih untuk move atau dig ke cell yang paling optimal, yaitu cell yang dapat mengantarkan worm kami bertemu dengan anggota worm kami yang lain. Sedangkan jika worm telah menemukan di area sekitarnya, maka worm akan memilih untuk menyerang lawan.

## Requirement Program dan Setting Up
Terdapat requirement dasar untuk menjalankan game ini, yaitu menginstall:
1. Java (minimal Java 8): https://www.oracle.com/java/technologies/javase/javasejdk8-downloads.html
2. IntellIJ IDEA: https://www.jetbrains.com/idea/
3. NodeJS: https://nodejs.org/en/download/

## How to Use
1. Open project ini menggunakan IntellIJ
2. Install project ini dengan plugin Maven yang sudah tersedia pada IntellIJ
3. Pindahkan file `..-jar-with-dependencies.jar` pada folder `target` ke folder `starter-pack`
4. Klik `run.bat` untuk menjalankan permainannya pada command prompt
5. Jika ingin melihat tampilan yang jauh lebih menarik, dapat menggunakan visualizer
6. Copy isi dari folder `matches-log` ke folder `Matches` yang ada pada folder visualizer
7. Klik `start-visualiser` untuk menjalankan visualizer

## Game Engine Source
1. Game engine: https://github.com/EntelectChallenge/2019-Worms.
2. Latest game engine: https://github.com/EntelectChallenge/2019-Worms/releases/tag/2019.3.2
3. Game rules: https://github.com/EntelectChallenge/2019-Worms/blob/develop/game-engine/game-rules.md.
4. Game visualizer: https://github.com/dlweatherhead/entelect-challenge-2019-visualiser/releases/tag/v1.0f1

## Author
1. Jesica (13519011)
2. Rahmah Khoirussyifa’ Nurdini (13519013)
3. Clarisa Natalia Edelin (13519213)
  
