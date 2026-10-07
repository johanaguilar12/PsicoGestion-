function StatusBadge({ status, compact = false }) {
  const normalizedStatus = status
    ?.toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");

  const className = [
    "status-badge",
    `status-badge--${normalizedStatus}`,
    compact ? "status-badge--compact" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return <span className={className}>{status}</span>;
}

export default StatusBadge;
