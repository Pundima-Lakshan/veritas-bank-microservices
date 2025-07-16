import { useQuery, type UseQueryOptions } from "@tanstack/react-query";
import {
  defaultSelect,
  type BaseRestResponse,
  type BaseRestResponseDefaultError,
} from "./utils";

export type ModifiedUseQueryOptions<T> = UseQueryOptions<
  BaseRestResponse<T>,
  BaseRestResponseDefaultError<T>,
  T
>;

interface QueryBuilderProps<T = unknown> {
  queryKey: ModifiedUseQueryOptions<T>["queryKey"];
  queryFn: ModifiedUseQueryOptions<T>["queryFn"];
  select?: ModifiedUseQueryOptions<T>["select"];
  enabled?: ModifiedUseQueryOptions<T>["enabled"];
  staleTime?: ModifiedUseQueryOptions<T>["staleTime"];
}

export const useQueryBuilder = <T = unknown>({
  queryKey,
  queryFn,
  select = defaultSelect,
  enabled = true,
  staleTime = Infinity,
}: QueryBuilderProps<T>) => {
  return useQuery<BaseRestResponse<T>, BaseRestResponseDefaultError<T>, T>({
    queryKey,
    queryFn,
    select,
    enabled,
    staleTime,
  });
};
