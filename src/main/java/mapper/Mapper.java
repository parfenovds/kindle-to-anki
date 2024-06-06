package mapper;


// Interface for generic mapper
public interface Mapper<S, T> {
  T mapFrom(S source);
}