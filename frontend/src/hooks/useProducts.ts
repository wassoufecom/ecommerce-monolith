import { useFetch } from './useFetch';
import type { ProductView, CategoryView } from '../api/types';

export const useProducts = () => useFetch<ProductView[]>('/products');
export const useCategories = () => useFetch<CategoryView[]>('/categories');
