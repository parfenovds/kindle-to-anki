package mapper;

public interface Mapper<S, T> {
  public T mapFrom(S source);
}
