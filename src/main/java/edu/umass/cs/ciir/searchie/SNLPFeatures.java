package edu.umass.cs.ciir.searchie;


import javax.annotation.Nullable;
import java.util.*;

/**
 * Make sense of Stanford-NLP feature naming.
 * cl = cluster
 * ng = character-ngram
 * w = word
 * sh = shape
 * p = part-of-speech
 * [-2],[-1],[0],[1],[2] indices are relative.
 * @author jfoley
 */
class SNLPFeatures {
  private static List<IndexOfParser> featureParsers = new ArrayList<>(Arrays.asList(
      new IndexOfParser("cl") {
        @Override public String parseValue(String input) {
          if("###|C".equals(input)) {
            return "#";
          }
          return null;
        }
      },
      new IndexOfParser("ng") {
        @Override public String parseValue(String input) {
          String ng = beforeIfEndsWith(input, "#|C");
          if(ng != null) {
            assert(ng.charAt(0) == '#');
            return ng.substring(1);
          }
          return null;
        }
      },
      new IndexOfParser("w[R]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-DISJN|C");
        }
      },
      new IndexOfParser("w[L]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-DISJP|C");
        }
      },
      new IndexOfParser("w[-1]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-PW|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQpW|CpC");
          return result;
        }
      },
      new IndexOfParser("p[1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-NTAG|C");
        }
      },
      new IndexOfParser("sh[-1,0,1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PCNTYPE|C");
        }
      },
      new IndexOfParser("sh[-1,0]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-PCTYPE|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-TYPES|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("w[0]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-WORD|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQW|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("c[1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-NDISTSIM|C");
        }
      },
      new IndexOfParser("sh[0]w[-2]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PPW_CTYPE|C");
        }
      },
      new IndexOfParser("w[0]p[-1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-W-PT|C");
        }
      },
      new IndexOfParser("sh[0,1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-CNTYPE|C");
        }
      },
      new IndexOfParser("sh[0]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-TYPE|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-TPS2|CpC");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQcS|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("w[0]p[1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-W-NT|C");
        }
      },
      new IndexOfParser("ocp") {
        @Override public String parseValue(String input) {
          if(input.contains("OCCURRENCE")) {
            if (input.startsWith("NO-OCCURRENCE-PATTERN")) {
              return "NO-OCCURRENCE-PATTERN";
            }
            if(input.endsWith("-X|C") || input.endsWith("-XY|C") || input.endsWith("-YX|C") || input.endsWith("-Y|C")) {
              return beforeIfEndsWith(input, "|C");
            }
          }
          return null;
        }
      },
      new IndexOfParser("w[1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-NW|C");
        }
      },
      new IndexOfParser("sh[0]w[-1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PW_CTYPE|C");
        }
      },
      new IndexOfParser("ti") {
        @Override public String parseValue(String input) {
          if("IS_TITLE|C".equals(input)) return "TITLE";
          return null;
        }
      },
      new IndexOfParser("sh[0]w[2]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-NNW_CTYPE|C");
        }
      },
      new IndexOfParser("c[0]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-DISTSIM|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQcDS|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("c[-1]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-PDISTSIM|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQpDS|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("c[-1,0]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PSEQpcDS|CpC");
        }
      },
      new IndexOfParser("p[0]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-TAG|C");
        }
      },
      new IndexOfParser("sh[0]w[1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-NW_CTYPE|C");
        }
      },
      new IndexOfParser("sh[-1]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-PTYPE|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-PSEQpS|CpC");
          return result;
        }
      },
      new IndexOfParser("sh[1]") {
        @Override public String parseValue(String input) {
          String result = beforeIfEndsWith(input, "-NTYPE|C");
          if(result != null) return result;
          result = beforeIfEndsWith(input, "-TNS1|CpC");
          if(result != null) return result;
          return result;
        }
      },
      new IndexOfParser("w[-1,0]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PSEQW2|CpC");
        }
      },
      new IndexOfParser("w[0]p[0]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-W-T|C");
        }
      },
      new IndexOfParser("p[-1]") {
        @Override public String parseValue(String input) {
          return beforeIfEndsWith(input, "-PTAG|C");
        }
      }
  ));

  public static Set<String> parseAllFeatures(Collection<String> inputFeatures) {
    HashSet<String> ft = new HashSet<>();
    for (String inputFeature : inputFeatures) {
      parseStanfordNERFeatures(inputFeature, ft);
    }
    return ft;
  }

  private static void parseStanfordNERFeatures( String fstr, Set<String> parsed_features ) {
    fstr = fstr.trim();
    if(fstr.equals("PSEQ|CpC")) return;
    boolean found = false;
    for ( IndexOfParser parser : featureParsers ) {
      String fvalue = parser.parse( fstr );
      if ( fvalue != null ) {
        parsed_features.add(fvalue);
        found = true;
        break;
      }
    }
    if(!found) {
      System.err.println(fstr);
    }
  }

  private static abstract class IndexOfParser {
    final String key;

    public IndexOfParser(String key) {
      this.key = key;
    }

    public final String beforeIfEndsWith(String input, String query) {
      if(input.endsWith(query)) {
        return input.substring(0, input.length()-query.length());
      }
      return null;
    }
    public String parse(String input) {
      String val = parseValue(input);
      if(val == null) return null;
      return key+"="+val;
    }

    public void extractToFielded(String input, Map<String, Set<String>> output) {
      String val = parseValue(input);
      if(val == null) return;
      output.computeIfAbsent(key, missing -> new HashSet<>()).add(val);
    }

    @Nullable
    abstract public String parseValue(String input);
  }
}
