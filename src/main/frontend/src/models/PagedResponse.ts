export interface PagedResponse<T> {
    totalPages: number
    currentPage: number
    pageSize: number
    totalItems: number
    content: T[]
}