//package it.unipi.dii.aide.mircv;
//
//import org.example.DAAT;
//
//import java.io.IOException;
//import java.util.Scanner;
//
//public class Main {
//
//    private static final int DEFAULT_DOC_TO_RETURN=10;
//
//    public static void main(String[] args) throws IOException {
//
//        Scanner scanner = new Scanner(System.in);
//        String[] options;
//
//        System.out.println("Information retrieval program has started! Welcome.");
//        // Loop to keep receiving queries
//        while (true) {
//
//            System.out.print("\nInsert your query (or '!q' to quit or '!h' to get help):\n" +
//                    ">> ");
//            String query = scanner.nextLine();
//
//            if(query.isEmpty()) {
//                System.out.println("Not a valid query\n");
//                continue;
//            }
//
//
//            options = query.split( "-");
//
//            if(options.length == 1) {
//
//                if (query.equalsIgnoreCase("!h")) {
//                    System.out.println("""
//                \nOptions available
//                '-c' conjunctive query mode or '-d' disjunctive query mode
//                optional '-10' or '-20' for top results to be returned (default value 10)""");
//                    continue;
//                } else if (query.equalsIgnoreCase("!q")) {
//                    System.out.println("Program closed, bye!");
//                    break;
//                }
//
//                System.out.println("Invalid query format, try again");
//            }
//
//            if(options.length == 2) {
//
//                //DA IMPLEMENTALRE - DEFAULT_DOC_TO_RETURN = 10
//                if(options[1].equals("c")){
//                    System.out.println("Implementare conjunctive mode con 10 doc da ritornare");
//                    DAAT.scoreQuery(options[0],true,DEFAULT_DOC_TO_RETURN,options[1]);
//                }
//                else if (options[1].equals("d")){
//                    System.out.println("Implementare disjunctive mode con 10 doc da ritornare");
//                }
//                else {
//                    if(options[1].equals("10") || options[1].equals("20")) {
//                        System.out.println("Invalid query format, try again");
//                    }
//                    else {
//                        System.out.println("Option non valid, only -c (conjunctive) or -d (disjunctive) supported");
//                    }
//                }
//            }
//
//            if(options.length == 3) {
//                // DA IMPLEMENTALRE
//                if (options[1].equals("c")) {
//                    if (options[2].equals("20"))
//                        System.out.println("Implementare conjunctive mode con 20");
//                    else
//                        System.out.println("Implementare conjunctive mode con 10");
//                } else if (options[1].equals("d")) {
//                    if (options[2].equals("20"))
//                        System.out.println("Implementare disjunctive mode con 20");
//                    else
//                        System.out.println("Implementare disjunctive mode con 10");
//                } else {
//                    System.out.println("Invalid query format, try again");
//                }
//            }
//
//            //scoring function use - opzionale
//
//            //query process (nel process controllare che options[0]
//            // sia formato da 2 o + parole per fare conjuctive o disjuctive
//
//            // doc return
//
//        }
//
//        scanner.close();
//
//
//    }
//}