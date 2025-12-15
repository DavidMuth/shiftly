// Returns start of current week (Monday 00:00) as Unix timestamp.
export const getStartOfWeek = () => {
  const now = new Date();
  const dayOfWeek = now.getDay(); // 0 (Sun) - 6 (Sat)
  const daysToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Handle Sunday edge case
  
  const monday = new Date(now);
  monday.setDate(now.getDate() + daysToMonday);
  monday.setHours(0, 0, 0, 0);
  
  return monday.getTime();
}

// Returns end of current week (Next Monday 00:00 = Sunday 24:00)
export const getEndOfWeek = () => {
  const now = new Date();
  const dayOfWeek = now.getDay();
  const daysToNextMonday = dayOfWeek === 0 ? 1 : 8 - dayOfWeek;
  
  const nextMonday = new Date(now);
  nextMonday.setDate(now.getDate() + daysToNextMonday);
  nextMonday.setHours(0, 0, 0, 0);
  
  return nextMonday.getTime();
}

// Get week boundaries for a given date
export const getWeekBoundaries = (date: Date) => {
  const dayOfWeek = date.getDay();
  const daysToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
  
  const monday = new Date(date);
  monday.setDate(date.getDate() + daysToMonday);
  monday.setHours(0, 0, 0, 0);
  
  const nextMonday = new Date(monday);
  nextMonday.setDate(monday.getDate() + 7);
  
  return {
    startTs: monday.getTime(),
    endTs: nextMonday.getTime()
  };
};