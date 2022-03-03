package calculators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegerMathTest {

    private static final Random RANDOM = new Random();

    private static final List<Long> FIBONACCI_NUMBERS = new ArrayList<>();

    static {
        FIBONACCI_NUMBERS.add(0L);
        FIBONACCI_NUMBERS.add(1L);
        long nextFibonacci = 1L;
        int currIndex = 2;
        while (nextFibonacci > 0) {
            FIBONACCI_NUMBERS.add(nextFibonacci);
            nextFibonacci = FIBONACCI_NUMBERS.get(currIndex - 1)
                    + FIBONACCI_NUMBERS.get(currIndex);
            currIndex++;
        }
    }

    @Test
    void testNeitherPrimeNorComposite() {
        assert !IntegerMath.isPrime(-1) : "-1 is not prime";
        assert !IntegerMath.isPrime(0) : "0 is not prime";
        assert !IntegerMath.isPrime(1) : "1 is not prime";
    }

    @Test
    void testNotPrime() {
        int start = RANDOM.nextInt(32) + 8;
        int end = RANDOM.nextInt(4096) + start;
        List<Integer> primes = EratosthenesSieve.listPrimes(start, end);
        String msgPart = " is not prime";
        String msg;
        String positivePrimeMsg
                = "Number said to be positive prime should be greater than 1";
        for (int p : primes) {
            assert p > 1 : positivePrimeMsg;
            int square = p * p;
            msg = square + msgPart;
            assert !IntegerMath.isPrime(square) : msg;
            msg = '-' + msg;
            assert !IntegerMath.isPrime(-square) : msg;
            int pronic = square + p;
            msg = pronic + msgPart;
            assert !IntegerMath.isPrime(pronic) : msg;
            msg = '-' + msg;
            assert !IntegerMath.isPrime(-pronic) : msg;
        }
    }

    @Test
    void testIsPrime() {
        System.out.println("isPrime");
        int start = (RANDOM.nextInt(4096) + 64) | 1;
        int end = (start + RANDOM.nextInt(128) + 2) | 1;
        double root = Math.sqrt(end);
        String msgPart = " should be recognized as prime";
        for (int i = start; i < end; i += 2) {
            int potentialDivisor = 3;
            boolean noDivisorFound = true;
            while (potentialDivisor <= root && noDivisorFound) {
                noDivisorFound = i % potentialDivisor != 0;
                potentialDivisor += 2;
            }
            if (noDivisorFound) {
                assert IntegerMath.isPrime(i) : (i + msgPart);
                assert IntegerMath.isPrime(-i) : (-i + msgPart);
            }
        }
    }

    @Test
    void testSmallPrimes() {
        List<Integer> smallPrimes = EratosthenesSieve.listPrimes(-128, 128);
        String msgPart = " should be recognized as prime";
        for (int p : smallPrimes) {
            assert IntegerMath.isPrime(p) : (p + msgPart);
        }
    }

    @Test
    void testBadBoundForRandomPrime() {
        int[] badBounds = {-1, 0, 1};
        for (int badBound : badBounds) {
            String msg = "Bad bound " + badBound
                    + " should have caused IllegalArgumentException";
            Throwable t = assertThrows(IllegalArgumentException.class, () -> {
                int badResult = IntegerMath.randomPrime(badBound);
                System.out.println("Bad bound " + badBound
                        + " somehow gave result " + badResult);
            }, msg);
            String excMsg = t.getMessage();
            assert excMsg != null : "Exception message should not be null";
            System.out.println("\"" + excMsg + "\"");
        }
    }

    @Test
    void testRandomPrimeCanGiveNegativePrime() {
        int p = IntegerMath.randomPrime(-128);
        assert IntegerMath.isPrime(p)
                : "Should have received a prime, not " + p;
        assert p < 0 : "Prime " + p + " should be negative";
    }

    @Test
    void testRandomPrime() {
        System.out.println("randomPrime");
        int size = RANDOM.nextInt(128) + 32;
        Set<Integer> set = new HashSet<>(size);
        int start = size * size;
        int end = start + size;
        for (int bound = start; bound < end; bound++) {
            int p = IntegerMath.randomPrime(bound);
            assert IntegerMath.isPrime(p)
                    : "Number " + p + " expected to be prime";
            assert p > 0 : "Prime " + p + " expected to be positive";
            set.add(p);
        }
        int expected = (int) Math.floor(0.9 * size);
        int actual = set.size();
        System.out.println("Successfully got " + size + " primes, " + actual
                + " distinct");
        String msg = "Expected at least " + expected + " distinct primes, got "
                + actual;
        assert actual >= expected : msg;
    }

    @Test
    void testEuclideanGCDSamePositiveNumber() {
        int expected = RANDOM.nextInt(1024) + 256;
        long actual = IntegerMath.euclideanGCD(expected, expected);
        assertEquals(expected, actual);
    }

    @Test
    void testEuclideanGCDSameNegativeNumber() {
        int a = -RANDOM.nextInt(1024) - 256;
        int expected = -a;
        long actual = IntegerMath.euclideanGCD(a, a);
        assertEquals(expected, actual);
    }

    @Test
    void testEuclideanGCD() {
        System.out.println("euclideanGCD");
        int expected = RANDOM.nextInt(4096) + 4;
        for (long i = -256; i < 256; i++) {
            long a = i * expected;
            long b = a + expected;
            long actual = IntegerMath.euclideanGCD(a, b);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testEuclideanGCDTwoConsecutiveFibonacciNumbers() {
        int lastIndex = FIBONACCI_NUMBERS.size() - 1;
        for (int i = 0; i < lastIndex; i++) {
            long a = FIBONACCI_NUMBERS.get(i);
            long b = FIBONACCI_NUMBERS.get(i + 1);
            String msg = "gcd(" + a + ", " + b + ") should be 1";
            assertEquals(1, IntegerMath.euclideanGCD(a, b), msg);
        }
    }

    private List<Long> sumsOfConsecutiveFibonacciNumbers(int n) {
        int capacity = FIBONACCI_NUMBERS.size() - n;
        List<Long> sums = new ArrayList<>(capacity);
        int index = 0;
        long sum = 1L;
        while (sum > 0) {
            sums.add(sum);
            sum = 0L;
            int stop = index + n;
            for (int i = index; i < stop; i++) {
                sum += FIBONACCI_NUMBERS.get(i);
            }
            index++;
        }
        sums.remove(0);
        return sums;
    }

    @Test
    void testEuclideanGCDConsecutiveSumsOfFibonacciNumbers() {
        int n = RANDOM.nextInt(7) + 3;
        List<Long> sums = sumsOfConsecutiveFibonacciNumbers(n);
        int lastIndex = sums.size() - 1;
        Set<Long> sumGCDs = new HashSet<>();
        long gcd = 1L;
        for (int i = 0; i < lastIndex; i++) {
            long a = sums.get(i);
            long b = sums.get(i + 1);
            gcd = IntegerMath.euclideanGCD(a, b);
            sumGCDs.add(gcd);
        }
        String msg = "GCD of two consecutive sums of " + n
                + " consecutive Fibonacci numbers is expected to be " + gcd
                + "?";
        assertEquals(1, sumGCDs.size(), msg);
    }

}
