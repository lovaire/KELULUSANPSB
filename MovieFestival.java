import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

class MovieFestival {
    static class Moviegoer {
        int id, money; 
        int watched;
        int watchedMovies;
        boolean isMember;
        
        
        Moviegoer(int id, boolean isMember, int money) {
            this.id = id;
            this.isMember = isMember;
            this.money = money;
            this.watchedMovies = 0;  
        }
    }
    
    static class Movie {
        int id, price, capacity, memberPercentage; 
        Queue<Moviegoer> queue = new PriorityQueue<>(new Comparator<Moviegoer>() {
            public int compare(Moviegoer m1, Moviegoer m2) {
              if (m1.watched != m2.watched) {
                return m1.watched - m2.watched; 
              }
              if (m1.isMember ^ m2.isMember) {
                return m1.isMember ? -1 : 1; 
              }
              return m1.id - m2.id;
            }
          });
        
        Movie(int id, int price, int capacity, int memberPercentage) {
            this.id = id;
            this.price = price;
            this.capacity = capacity;  
            this.memberPercentage = memberPercentage;
        }
        
        void book(Moviegoer m) {
            if (m.money >= price && queue.size() < capacity) {
              queue.add(m);
            } 
          }
        
          void play() {
            if (queue.isEmpty()) {
              System.out.println(-1);
              return;
            }
      
            Stack<Integer> viewers = new Stack<>();
      
            while (!queue.isEmpty() && viewers.size() < capacity) {
              Moviegoer m = queue.remove();
              viewers.push(m.id);
              m.watched++;
              m.money -= price;
            }
      
            while (!viewers.isEmpty()) {
              System.out.print(viewers.pop() + " ");
            }
            System.out.println();
          }
        
          int getViewerPos(Moviegoer m) {
            if (!queue.contains(m)) return -1;
      
            int pos = 1;
            for (Moviegoer viewer : queue) {
              if (viewer == m) return pos;
              pos++; 
            }
            return -1;
          }
    }
    
    static void joinFestival(int N, List<Movie> movies, List<Moviegoer> moviegoers) {

        int[] maxPrices = new int[N];
        int[][] possibilities = new int[N][];
    
        for (int i=0; i<N; i++) {
          maxPrices[i] = Integer.MIN_VALUE; 
          possibilities[i] = new int[movies.size()];
        }
    
        int index = 0;
        for (Movie m : movies) {
          possibilities[0][index] = m.id % 3 + 1;
          if (m.price > maxPrices[0]) {
            maxPrices[0] = m.price;  
          }
          index++;
        }
    
        for (int i=1; i<N; i++) {
    
          index = 0;
          for (int[] genreSet : possibilities) {
            
            for (Movie m : movies) {
    
              boolean valid = true;
              if (i > 0) {
                for (int j=0; j<i; j++) {
                  if (genreSet[j] == m.id % 3 + 1) {
                    valid = false;
                    break;
                  } 
                }
              }
    
              if (valid && m.price > maxPrices[i]) {
                genreSet[index] = m.id % 3 + 1;
                maxPrices[i] = m.price;
              }
    
            }
            index++;
    
          }}
    
        }
    
    public static void main(String[] args) {
    
        Scanner sc = new Scanner(System.in);
    
        int days = sc.nextInt();
        
        List<Movie> horrorMovies = new ArrayList<>(); 
        List<Movie> actionMovies = new ArrayList<>();
        List<Movie> scifiMovies = new ArrayList<>();

        List<Movie> movies = Stream.of(horrorMovies, actionMovies, scifiMovies)
                                   .flatMap(List::stream)
                                   .collect(Collectors.toList());
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt(); 
            int capacity = sc.nextInt();
            int percentage = sc.nextInt();
            horrorMovies.add(new Movie(i+1, price, capacity, percentage)); 
        }
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt();
            int capacity = sc.nextInt(); 
            int percentage = sc.nextInt();
            actionMovies.add(new Movie(i+days+1, price, capacity, percentage));
        }
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt();
            int capacity = sc.nextInt();
            int percentage = sc.nextInt();
            scifiMovies.add(new Movie(i+days*2+1, price, capacity, percentage)); 
        }
        
        int numMoviegoers = sc.nextInt();
        
        List<Moviegoer> moviegoers = new ArrayList<>(); 
        for (int i=0; i<numMoviegoers; i++) {
            String type = sc.next();
            int money = sc.nextInt();
            
            boolean isMember = type.equals("M"); 
            moviegoers.add(new Moviegoer(i+1, isMember, money));
        }
        
        int queries = sc.nextInt();
        
        while (queries-- > 0) {

            String action = sc.next();
        
            if (action.equals("J")) {
                
                int type = sc.nextInt();
                int day = sc.nextInt();
                
                joinFestival(days, movies, moviegoers);
            
            } else if (action.equals("B")) {
                
                int moviegoerId = sc.nextInt() - 1;
                int movieId = sc.nextInt() - 1;
                
                Movie movie;
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days); 
                } else {
                    movie = scifiMovies.get(movieId-2*days);
                }
                
                movie.book(moviegoers.get(moviegoerId));
                System.out.println(movie.queue.size());
                
            } else if (action.equals("P")) {
                int movieId = sc.nextInt() - 1;
                
                Movie movie; 
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days);
                } else {
                    movie = scifiMovies.get(movieId-2*days);
                }
                
                movie.play();
                
            } else if (action.equals("T")) {
                int type = sc.nextInt();
                int moviegoerId = sc.nextInt() - 1;
                int movieId = sc.nextInt() - 1;
                
                Movie movie;
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days);
                } else {
                    movie = scifiMovies.get(movieId-2*days); 
                }
                
                System.out.println(movie.getViewerPos(moviegoers.get(moviegoerId)));
                 
            } else {
                int type = sc.nextInt();
                int day = sc.nextInt();

                joinFestival(days, Stream.of(horrorMovies, actionMovies, scifiMovies)
                                    .flatMap(x -> x.stream())
                                    .collect(Collectors.toList()),
                                    moviegoers);
            }
            
            
        }
        
        sc.close();
    }
}