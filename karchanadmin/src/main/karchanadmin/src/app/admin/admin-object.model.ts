export interface AdminObject<T> {
    getIdentifier(): T;
    setIdentifier(t: T): void;
}
