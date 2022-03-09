BEGIN;

DROP DOMAIN IF EXISTS uint_256 CASCADE;
DROP DOMAIN IF EXISTS UINT256 CASCADE;
DROP DOMAIN IF EXISTS MONETARY CASCADE;

CREATE DOMAIN UINT256 AS NUMERIC NOT NULL
  CHECK (VALUE >= 0 AND VALUE < 2^256)
  CHECK (SCALE(VALUE) = 0);

CREATE DOMAIN MONETARY AS NUMERIC(36,18) NOT NULL
  CHECK (VALUE >= 0 AND VALUE < 10^18)
;


DROP TABLE IF EXISTS node CASCADE;
/*
CREATE TABLE node (
  public_key BYTEA NOT NULL PRIMARY KEY,
  private_key BYTEA NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp()
);
*/

DROP TABLE IF EXISTS payment_addr CASCADE;
CREATE TABLE payment_addr (
  label TEXT,
  public_key BYTEA NOT NULL PRIMARY KEY,
  private_key BYTEA NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp()
);

/* amount = uint256 32 bytes */

DROP TABLE IF EXISTS tx CASCADE;
CREATE TABLE tx (
  txid BYTEA NOT NULL PRIMARY KEY,
  from_public_key BYTEA NOT NULL REFERENCES payment_addr (public_key) ON DELETE CASCADE,
  to_public_key BYTEA NOT NULL REFERENCES payment_addr (public_key) ON DELETE CASCADE,
  amount MONETARY NOT NULL, --UINT256,
  --amount DECIMAL(36,18),
  created TIMESTAMP NOT NULL,
  signature BYTEA NOT NULL
);

DROP VIEW IF EXISTS view_tx;
DROP VIEW IF EXISTS tx_base64;
CREATE VIEW tx_base64 AS
SELECT
  encode(txid, 'base64') as txid,
  encode(from_public_key, 'base64') as from_public_key,
  encode(to_public_key, 'base64') as to_public_key,
  amount,
  created,
  encode(signature, 'base64') as signature,
  encode(sha256(from_public_key || to_public_key || amount::text::bytea || created::text::bytea), 'base64') as check_txid,
  encode(created::text::bytea, 'base64') as created_b64,
  encode(amount::text::bytea, 'base64') as amount_b64
FROM tx
;


DROP TABLE IF EXISTS tx_in CASCADE;
CREATE TABLE tx_in (
  txid BYTEA NOT NULL REFERENCES tx (txid),
  index INTEGER NOT NULL,
  UNIQUE (txid, index)
);


DROP TABLE IF EXISTS utxo CASCADE;
CREATE TABLE utxo (
  txid BYTEA NOT NULL REFERENCES tx (txid), -- orig. transaction that produced the utxo
  public_key BYTEA NOT NULL REFERENCES payment_addr (public_key), -- the wallet who owns the $
  amount MONETARY
);


COMMIT;
