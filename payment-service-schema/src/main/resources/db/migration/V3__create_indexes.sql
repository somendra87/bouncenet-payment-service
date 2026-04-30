CREATE INDEX idx_payments_user ON payments(external_user_id);
CREATE INDEX idx_payments_entity ON payments(external_entity_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE INDEX idx_payment_orders_payment_id ON payment_orders(payment_id);

CREATE INDEX idx_payment_txn_payment_id ON payment_transactions(payment_id);

CREATE INDEX idx_ledger_payment_id ON ledger_entries(payment_id);

CREATE INDEX idx_refunds_payment_id ON refunds(payment_id);

CREATE INDEX idx_payout_entity ON payouts(external_entity_id);
