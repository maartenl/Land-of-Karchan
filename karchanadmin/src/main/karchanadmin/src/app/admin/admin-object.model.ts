export interface AdminObject<T> {
    getIdentifier(): T | null;
    setIdentifier(t: T): void;
    getType(): string;
}
