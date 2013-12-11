package fr.pfgen.cgh.shared.sharedUtils;

public interface Transformer<T2,T1> {
	public T1 transform(T2 object);
}
