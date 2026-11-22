-- Optional institutional information; never invent or prefill commercial data.
ALTER TABLE store_information
  ADD COLUMN opening_hours VARCHAR(500),
  ADD COLUMN address VARCHAR(500),
  ADD COLUMN region VARCHAR(180);
