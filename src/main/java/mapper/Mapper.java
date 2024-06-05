package mapper;

public interface Mapper<S, T> {
  T mapFrom(S source);
}
