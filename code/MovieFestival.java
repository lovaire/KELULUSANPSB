import java.util.*;

public class MovieFestival {
    static class Movie {
        int id, price, capacity, prioritySeats;
        PriorityQueue<MovieGoer> bookingQueue;

        public Movie(int id, int price, int capacity, int priorityPercentage) {
            this.id = id;
            this.price = price;
            this.capacity = capacity;
            this.prioritySeats = (int) Math.ceil(capacity * priorityPercentage / 100.0);
            this.bookingQueue = new PriorityQueue<>((a, b) -> {
                if (a.isMember != b.isMember) {
                    return a.isMember ? -1 : 1;
                } else {
                    return Integer.compare(a.moviesAttended, b.moviesAttended);
                }
            });
        }
    }

    static class MovieGoer {
        int id, money, moviesAttended;
        boolean isMember;

        public MovieGoer(int id, int money, boolean isMember) {
            this.id = id;
            this.money = money;
            this.isMember = isMember;
            this.moviesAttended = 0;
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        Movie[][] movies = new Movie[3][N];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < N; j++) {
                int price = scanner.nextInt();
                int capacity = scanner.nextInt();
                int priorityPercentage = scanner.nextInt();
                movies[i][j] = new Movie(j + 1, price, capacity, priorityPercentage);
            }
        }

        int M = scanner.nextInt();
        MovieGoer[] movieGoers = new MovieGoer[M];
        for (int i = 0; i < M; i++) {
            String type = scanner.next();
            int money = scanner.nextInt();
            boolean isMember = type.equals("M");
            movieGoers[i] = new MovieGoer(i + 1, money, isMember);
        }

        int T = scanner.nextInt();
        for (int i = 0; i < T; i++) {
            String action = scanner.next();
            if (action.equals("B")) {
                int movieGoerId = scanner.nextInt();
                int movieId = scanner.nextInt();
                bookMovie(movies, movieGoers[movieGoerId - 1], movieId);
            } else if (action.equals("P")) {
                int movieId = scanner.nextInt();
                playMovie(movies, movieId);
            } else if (action.equals("T")) {
                int movieGoerId = scanner.nextInt();
                int movieId = scanner.nextInt();
                trackBooking(movies, movieGoerId, movieId);
            } else if (action.equals("J")) {
                int type = scanner.nextInt();
                joinFestival(movies, N, type);
            }
        }
        scanner.close();
    }

    static void bookMovie(Movie[][] movies, MovieGoer movieGoer, int movieId) {
        Movie movie = null;
        for (Movie[] genre : movies) {
            for (Movie m : genre) {
                if (m.id == movieId) {
                    movie = m;
                    break;
                }
            }
        }
        if (movie != null && movieGoer.money >= movie.price) {
            if (movie.bookingQueue.size() < movie.capacity) {
                int prioritySeatsLeft = movie.prioritySeats - countPrioritySeats(movie);
                if (movieGoer.isMember && prioritySeatsLeft > 0) {
                    if (movie.bookingQueue.size() < movie.prioritySeats) {
                        movie.bookingQueue.offer(movieGoer);
                    } else {
                        movie.bookingQueue.add(movieGoer);
                    }
                } else {
                    movie.bookingQueue.offer(movieGoer);
                }
                movieGoer.money -= movie.price;
                System.out.println(movie.bookingQueue.size());
            } else {
                System.out.println("-1");
            }
        } else {
            System.out.println("-1");
        }
    }

    static int countPrioritySeats(Movie movie) {
        int count = 0;
        for (MovieGoer goer : movie.bookingQueue) {
            if (goer.isMember) {
                count++;
            }
        }
        return count;
    }

    static void playMovie(Movie[][] movies, int movieId) {
        Movie movie = null;
        for (Movie[] genre : movies) {
            for (Movie m : genre) {
                if (m.id == movieId) {
                    movie = m;
                    break;
                }
            }
        }
        if (movie != null && !movie.bookingQueue.isEmpty()) {
            int seatsToFill = Math.min(movie.capacity - movie.bookingQueue.size(), movie.capacity);
            boolean movieGoerFound = false;
            for (int i = 0; i < seatsToFill; i++) {
                MovieGoer movieGoer = movie.bookingQueue.poll();
                if (movieGoer != null) {
                    movieGoer.moviesAttended++;
                    movieGoer.money -= movie.price;
                    System.out.println(movieGoer.id);
                    movieGoerFound = true;
                }
            }
            if (!movieGoerFound) {
                System.out.println("-1");
            }
        } else {
            System.out.println("-1");
        }
    }

    static void trackBooking(Movie[][] movies, int movieGoerId, int movieId) {
        Movie movie = null;
        for (Movie[] genre : movies) {
            for (Movie m : genre) {
                if (m.id == movieId) {
                    movie = m;
                    break;
                }
            }
        }
        if (movie != null) {
            int position = 1;
            for (MovieGoer movieGoer : movie.bookingQueue) {
                if (movieGoer.id == movieGoerId) {
                    System.out.println(position);
                    return;
                }
                position++;
            }
        }
        System.out.println("-1");
    }

  static void joinFestival(Movie[][] movies, int N, int type) {
      int[][][] dp = new int[N + 1][3][3];
      int[][][] lastGenre = new int[N + 1][3][3];
  
      for (int i = 0; i <= N; i++) {
          for (int j = 0; j < 3; j++) {
              Arrays.fill(dp[i][j], Integer.MIN_VALUE);
              Arrays.fill(lastGenre[i][j], -1);
          }
      }
  
      dp[0][0][0] = 0;
  
      for (int i = 1; i <= N; i++) {
          for (int j = 0; j < 3; j++) {
              for (int k = 0; k < 3; k++) {
                  for (int prevK = 0; prevK < 3; prevK++) {
                      if (j != k) {
                          int maxPrice = getMaxPrice(movies[j], i);
                          int totalCost = dp[i - 1][prevK][j] + maxPrice;
                          if (totalCost > dp[i][j][k]) {
                              dp[i][j][k] = totalCost;
                              lastGenre[i][j][k] = prevK;
                          }
                      }
                  }
              }
          }
      }
  
      int maxCost = Integer.MIN_VALUE;
      int[] bestOrder = new int[N];
      int startGenre = 0, endGenre = 0;
      for (int j = 0; j < 3; j++) {
          for (int k = 0; k < 3; k++) {
              if (j != k) {
                  if (dp[N][j][k] > maxCost) {
                      maxCost = dp[N][j][k];
                      startGenre = j;
                      endGenre = k;
                  }
              }
          }
      }
  
      int curGenre = startGenre;
      for (int i = N - 1; i >= 0; i--) {
          bestOrder[i] = curGenre + 1;
          int prevGenre = lastGenre[i + 1][curGenre][endGenre];
          endGenre = curGenre;
          curGenre = prevGenre;
      }
  
      if (type == 0) {
          System.out.println(maxCost);
      } else {
          for (int genre : bestOrder) {
              System.out.print(genre + " ");
          }
          System.out.println();
      }
  }
      static int getMaxPrice(Movie[] movies, int day) {
          int maxPrice = 0;
          for (Movie movie : movies) {
              if (movie.id == day) {
                  maxPrice = Math.max(maxPrice, movie.price);
              }
          }
          return maxPrice;
      }
  }
