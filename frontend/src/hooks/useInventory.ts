import { useFetch } from './useFetch';
import type { StockView } from '../api/types';

export const useInventory = () => useFetch<StockView[]>('/inventory');
