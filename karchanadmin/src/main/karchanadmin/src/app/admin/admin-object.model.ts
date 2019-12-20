export interface AdminObject<T> {
    getIdentifier(): T;
    setIdentifier(T): void;
}